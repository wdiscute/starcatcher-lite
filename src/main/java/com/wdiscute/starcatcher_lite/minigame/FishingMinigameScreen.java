package com.wdiscute.starcatcher_lite.minigame;

import com.mojang.blaze3d.platform.InputConstants;
import com.wdiscute.starcatcher_lite.*;
import com.wdiscute.starcatcher_lite.fish.Difficulty;
import com.wdiscute.starcatcher_lite.fish.Textures;
import com.wdiscute.starcatcher_lite.registry.*;
import com.wdiscute.starcatcher_lite.data.network.SCLSBFishingCompletedPayload;
import com.wdiscute.utils.ScreenUtils;
import com.wdiscute.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.Tags;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

import java.util.*;

public class FishingMinigameScreen extends Screen implements GuiEventListener
{
    public ScreenUtils.Image tank;
    public ScreenUtils.Image rod;
    public ScreenUtils.Image handle;
    public ScreenUtils.Image buttons;
    public ScreenUtils.Image treasure;
    public ScreenUtils.Image wheels;

    public boolean renderBlur = true;

    public Difficulty difficulty;

    public final ItemStack item;

    public final InteractionHand handToSwing;

    public int hp;
    public float penalty;
    public float decay;

    public float kimbeMarkerPos = 0;
    public int kimbeMarkerColor = 0x00000000;

    public int gracePeriod = Integer.MAX_VALUE;

    public float handleSpeed;
    public float handleBaseSpeed;

    public int tickCount = 0;
    public float handlePos = 0;
    public int currentRotation = 1;
    public float partial;
    public float hitDelay;

    public float progress = 20;
    public float progressSmooth = 20;

    public boolean perfectCatch = true;
    public int consecutiveHits = 0;

    List<HitFakeParticle> hitParticles = new ArrayList<>();

    // Nikdo53 fields, these are mine don't steal them
    // Starcatcher Lite comment - i dont know why these are needed so they are staying
    public final int holdingDelay = 6;
    public int holdingTicks = 0;
    protected boolean isHoldingKey = false;
    protected boolean isHoldingMouse = false;

    protected final List<ActiveSweetSpot> activeSweetSpots = new ArrayList<>();
    protected final List<ActiveSweetSpot> spotsToAdd = new ArrayList<>(); // delays the adding process to avoid concurrency exceptions

    public static void start(ItemStack itemStack, Difficulty difficulty)
    {
        Minecraft.getInstance().setScreenAndShow(new FishingMinigameScreen(itemStack, difficulty));
    }

    public FishingMinigameScreen(ItemStack itemStack, Difficulty difficulty)
    {
        super(Component.empty());

        if (ModList.get().isLoaded("scauto")) throw new RuntimeException();

        handToSwing = Minecraft.getInstance().player.getMainHandItem().is(Tags.Items.RODS) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        Textures textures = itemStack.getItem().builtInRegistryHolder().getData(SCLDataMaps.MINIGAME_TEXTURES);

        if (textures == null)
            textures = Textures.DEFAULT;

        tank = textures.tank();
        rod = textures.rod();
        handle = textures.handle();
        buttons = textures.buttons();
        treasure = textures.treasure();
        wheels = textures.wheels();

        hitDelay = SCLConfig.HIT_DELAY.get().floatValue();

        this.difficulty = difficulty;

        this.item = itemStack;

        //tank texture change
        Player player = Minecraft.getInstance().player;

        //difficulty
        this.handleSpeed = (float) (difficulty.speed() * player.getAttributeValue(SCLAttributes.HANDLE_ROTATION_SPEED_MULTIPLIER) * SCLConfig.HANDLE_SPEED_MULTIPLIER.get());
        this.handleBaseSpeed = (float) (difficulty.speed() * player.getAttributeValue(SCLAttributes.HANDLE_ROTATION_SPEED_MULTIPLIER) * SCLConfig.HANDLE_SPEED_MULTIPLIER.get());
        this.penalty = (int) (difficulty.penalty() * player.getAttributeValue(SCLAttributes.PENALTY_MULTIPLIER) * SCLConfig.PENALTY_MULTIPLIER.get());
        this.decay = (float) (difficulty.decay() * player.getAttributeValue(SCLAttributes.BASE_DECAY_MULTIPLIER) * SCLConfig.DECAY_RATE_MULTIPLIER.get());
        this.hp = (int) (difficulty.hp() * player.getAttributeValue(SCLAttributes.REQUIRED_SCORE_MULTIPLIER) * SCLConfig.HP_RATE_MULTIPLIER.get());

        tick();

        //add every sweet spot from fp Json which is registered
        for (Difficulty.SweetSpot ss : difficulty.sweetSpots())
        {
            var newSweetSpot = new ActiveSweetSpot(this, ss);
            addSweetSpot(newSweetSpot);
        }
    }

    public List<ActiveSweetSpot> getActiveSweetSpots()
    {
        return activeSweetSpots;
    }

    public void addSweetSpot(ActiveSweetSpot ass)
    {
        if (!ass.removed) this.spotsToAdd.add(ass);
    }

    public int getRandomFreePosition(int sizeOfTheSweetspotToPlace)
    {
        int posBeingChecked = Utils.r.nextInt(360);

        //find the closest available pos
        for (int i = 0; i < 180; i++)
        {

            //check left and right
            for (int j = 1; j > -2; j -= 2)
            {

                int checkPos = clampPos(posBeingChecked + (i * j));

                if (activeSweetSpots.stream().noneMatch(s -> doDegreesOverlapWithLeeway(checkPos, s.pos, (s.thickness + sizeOfTheSweetspotToPlace) / 2)
                ))
                {
                    return checkPos;
                }
            }
        }
        return Utils.r.nextInt(360);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float a)
    {
        if (renderBlur)
            super.extractBackground(g, mouseX, mouseY, a);

        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

        Matrix3x2fStack poseStack = g.pose();
        partial = partialTick;

        poseStack.pushMatrix();
        poseStack.translate(width >> 1, height >> 1);

        poseStack.translate((float) SCLConfig.MINIGAME_X_OFFSET.getAsDouble(), (float) SCLConfig.MINIGAME_Y_OFFSET.getAsDouble());

        poseStack.scale(((float) SCLConfig.MINIGAME_RENDER_SCALE.getAsDouble()), ((float) SCLConfig.MINIGAME_RENDER_SCALE.getAsDouble()));

        poseStack.translate(-width >> 1, -height >> 1);

        int centerX = width / 2;
        int centerY = height / 2;

        //render tank background
        tank.render(g, centerX - 44 - 106, centerY - 58);

        //render wheel background
        wheels.render(g, centerX - 32, centerY - 32, 0, 0, 64, 64);

        //render spacebar
        boolean bigBar = font.width(SCLKeymappings.MINIGAME_HIT.getKey().getDisplayName().getString()) > 18;

        int yOffset = (isHoldingKey ? 16 : 0) + (bigBar ? 0 : 32);
        buttons.render(g, centerX - 24, centerY + 40,
                0, yOffset,
                48, 16);

        //render spacebar keybind name
        ScreenUtils.centeredText(g, font, SCLKeymappings.MINIGAME_HIT.getKey().getDisplayName(),
                centerX, centerY + 42 + (isHoldingKey ? 2 : 0),
                0xff546170, false);

        //render all sweet spots
        activeSweetSpots.forEach(ass -> renderSweetSpot(ass, g, partialTick, poseStack));

        //render wheel second layer
        wheels.render(g, centerX - 32, centerY - 32, 64, 0, 64, 64);

        //render handle
        renderHandle(g, poseStack, partialTick);

        //render kimbe marker
        renderKimbeMarker(g);

        //silver thing on top
        wheels.render(g, centerX - 16, centerY - 16, 128, 16, 32, 32);

        //fishing rod
        rod.render(g, centerX - 118, centerY - 116,
                16, 0, 96, 112);

        progressSmooth += ((progress - progressSmooth) / 6) * a;

        float yoffset = progressSmooth == 0 ? 0 : (progressSmooth / (float) hp * 77);

        //fishing line
        rod.render(g, centerX - 6 - 102, centerY - 56 - 24,
                0, yoffset - 4,
                16, (int) (112 - yoffset + 5));

        //item being fished
        poseStack.pushMatrix();
        poseStack.translate(0, -yoffset);

        ScreenUtils.item(g, item, centerX - 8 - 100, centerY - 8 + 35);
        poseStack.popMatrix();

        //render sweet spots foreground
        activeSweetSpots.forEach(ass -> ass.behaviour.renderForeground(g, partialTick, width, height, this, ass));

        //render particles
        hitParticles.forEach(p -> p.render(g, width, height));

        poseStack.popMatrix();

        if (SCLConfig.DEBUG_MINIGAME.get())
        {
            int yOffsetASS = 10;
            ScreenUtils.text(g, font,
                    "active sweet-spots: " + activeSweetSpots.size(), width / 2 + 90, yOffsetASS, 0xffffff00);

            for (ActiveSweetSpot ass : activeSweetSpots)
            {
                yOffsetASS += 10;
                ScreenUtils.text(g, font,
                        "texture: " + (
                                ass.texture.id().getPath().contains("/")
                                        ? ass.texture.id().getPath().substring(ass.texture.id().getPath().lastIndexOf('/') + 1)
                                        : ass.texture.id().getPath()
                        ), width / 2 + 90, yOffsetASS, 0xffffffff);
                yOffsetASS += 10;
                ScreenUtils.text(g, font,
                        "rew: " + ass.reward + " / van: " + ass.vanishingRate + " / mov: " + ass.movingRate + " / pos: " + ass.pos, width / 2 + 90, yOffsetASS, 0xffffffff);
                yOffsetASS += 10;
                ScreenUtils.text(g, font,
                        "col: " + ass.particleColor + " / thick:" + ass.thickness + " / alpha: " + ass.alpha, width / 2 + 90, yOffsetASS, 0xffffffff);
                yOffsetASS += 10;
                ScreenUtils.text(g, font,
                        "behaviour: " + (
                                ass.behaviour.toString().contains(".")
                                        ? ass.behaviour.toString().substring(ass.behaviour.toString().lastIndexOf('.') + 1)
                                        : ass.behaviour.toString()
                        ), width / 2 + 90, yOffsetASS, 0xffffffff);
                yOffsetASS += 10;
            }

            //fish
            ScreenUtils.text(g, font,
                    "fish: ", width / 2 - 100, height / 2 + 80, 0xffffff00);
            ScreenUtils.text(g, font,
                    "item: " + item, width / 2 - 100, height / 2 + 90, 0xffffffff);
            ScreenUtils.text(g, font,
                    "progress: " + progress + "/" + hp, width / 2 - 100, height / 2 + 100, 0xffffffff);


            ScreenUtils.text(g, font,
                    "fp: ", 10, height - 100, 0xffffff00);
            ScreenUtils.text(g, font,
                    "penalty: " + penalty, 10, height - 90, 0xffffffff);
            ScreenUtils.text(g, font,
                    "handle speed: " + handleSpeed, 10, height - 80, 0xffffffff);
            ScreenUtils.text(g, font,
                    "handle pos: " + handlePos, 10, height - 70, 0xffffffff);
            ScreenUtils.text(g, font,
                    "decay: " + decay, 10, height - 60, 0xffffffff);
            ScreenUtils.text(g, font,
                    "perfect catch: " + perfectCatch, 10, height - 50, 0xffffffff);
            ScreenUtils.text(g, font,
                    "cons. hits: " + consecutiveHits, 10, height - 40, 0xffffffff);
            ScreenUtils.text(g, font,
                    "particles: " + hitParticles.size(), 10, height - 30, 0xffffffff);
            ScreenUtils.text(g, font,
                    "grace: " + gracePeriod, 10, height - 20, 0xffffffff);
            ScreenUtils.text(g, font,
                    "tick: " + tickCount, 10, height - 10, 0xffffffff);
        }
    }

    public void renderSweetSpot(ActiveSweetSpot ass, GuiGraphicsExtractor guiGraphics, float partialTick, Matrix3x2fStack poseStack)
    {
        float centerX = (float) width / 2;
        float centerY = (float) height / 2;

        poseStack.pushMatrix();

        poseStack.translate(centerX, centerY);

        poseStack.rotate((float) Math.toRadians(ass.pos + (partialTick * ass.movingRate) * ass.currentRotation));

        ass.behaviour.render(guiGraphics, poseStack, partialTick, this, ass);

        poseStack.popMatrix();
    }

    public void renderKimbeMarker(GuiGraphicsExtractor g)
    {
        Matrix3x2fStack poseStack = g.pose();
        poseStack.pushMatrix();

        float centerX = (float) width / 2;
        float centerY = (float) height / 2;

        poseStack.translate(centerX, centerY);
        poseStack.rotate((float) Math.toRadians(kimbeMarkerPos));
        poseStack.translate(-centerX, -centerY);

        ScreenUtils.outline(g, (int) centerX, (int) centerY - 34, 2, 34, kimbeMarkerColor);

        poseStack.popMatrix();
    }

    public void renderHandle(GuiGraphicsExtractor guiGraphics, Matrix3x2fStack poseStack, float partialTickDONOTUSE)
    {
        poseStack.pushMatrix();

        float centerX = (float) width / 2;
        float centerY = (float) height / 2;

        poseStack.translate(centerX, centerY);

        float v = handlePos + ((handleSpeed * partial) * currentRotation);
        poseStack.rotate((float) Math.toRadians(v));

        poseStack.translate(0, -16);

        handle.render(guiGraphics, -16, -32);

        poseStack.translate(0, 16);

        poseStack.popMatrix();
    }

    @Override
    public boolean keyReleased(KeyEvent event)
    {
        if (event.key() == SCLKeymappings.MINIGAME_HIT.getKey().getValue())
        {
            isHoldingKey = false;
            holdingTicks = 0;
        }

        return super.keyReleased(event);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        isHoldingMouse = false;
        holdingTicks = 0;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        inputPressed();
        isHoldingMouse = true;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event)
    {
        //closes when pressing E unless keybind is set to E
        InputConstants.Key mouseKey = InputConstants.getKey(event);
        if (this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey) && !SCLKeymappings.MINIGAME_HIT.getKey().equals(mouseKey))
        {
            //play failed sound
            this.onClose();
            return true;
        }

        //hit input
        if (SCLKeymappings.MINIGAME_HIT.isActiveAndMatches(mouseKey))
        {
            if (!isHoldingKey) inputPressed();

            isHoldingKey = true;
        }

        return super.keyPressed(event);
    }

    public void inputPressed()
    {
        if (gracePeriod > 0) gracePeriod = 0;

        Minecraft.getInstance().player.swing(handToSwing, SwingAnimation.DEFAULT, true);

        boolean hitSomething = false;

        Vec3 pos = Minecraft.getInstance().player.position();
        ClientLevel level = Minecraft.getInstance().level;

        kimbeMarkerPos = getHandlePosPrecise();


        for (ActiveSweetSpot ass : activeSweetSpots.reversed())
        {
            //if can hit spot
            if (ass.canHit && doDegreesOverlapWithLeeway(getHandlePosPrecise(), ass.pos, ass.thickness / 2))
            {
                consecutiveHits++;
                hitSomething = true;

                //trigger sweet-spot behaviour on hit
                ass.behaviour.onHit(this, ass);
                break;
            }
        }


        if (!hitSomething)
        {
            perfectCatch = false;
            consecutiveHits = 0;

            if (SCLConfig.ENABLE_MISS_SOUND.get())
                level.playLocalSound(pos.x, pos.y, pos.z, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1, 1, false);

            activeSweetSpots.forEach(ass -> ass.behaviour.onMiss(this, ass));

            //kimbe marker
            kimbeMarkerColor = 0xffff6767;
            kimbeMarkerPos = getHandlePosPrecise();

            progress -= penalty;
        }
        else
        {
            kimbeMarkerColor = 0xff2ce17d;
            kimbeMarkerPos = getHandlePosPrecise();
        }
    }

    public float getHandlePosPrecise()
    {
        float handlePosPrecise = (handlePos + ((handleSpeed * partial) * currentRotation));

        handlePosPrecise += hitDelay * handleSpeed * currentRotation;
        return handlePosPrecise;
    }

    public static boolean doDegreesOverlapWithLeeway(float degrees1, float degrees2, int leeway)
    {
        boolean b = Math.abs(degrees2 - degrees1) < (float) leeway;
        boolean b2 = Math.abs(degrees2 - degrees1) > 360 - ((float) leeway);
        return b || b2;
    }

    @Override
    public void tick()
    {
        //kimbe marker
        {
            //decrease kimbe markers alpha
            int color = kimbeMarkerColor;

            int alpha = (color >>> 24) & 0xff;


            alpha = Math.max(0, alpha - 20);

            color = (color & 0x00ffffff) | (alpha << 24);

            kimbeMarkerColor = color;
        }


        if (isHoldingInput())
        { //mimics the keyboard behavior
            holdingTicks++;
            if (holdingTicks > holdingDelay) inputPressed();
        }

        //tick behaviour
        activeSweetSpots.forEach(ass -> ass.behaviour.tick(this, ass));

        //remove activeSweetSpots marked for removal
        activeSweetSpots.removeIf(ass ->
        {
            if (ass.removed) ass.behaviour.onRemove(this, ass);
            return ass.removed;
        });

        //add queued sweetspots
        activeSweetSpots.addAll(spotsToAdd);
        //trigger onAdd of SweetSpotBehaviour
        spotsToAdd.forEach(o -> o.behaviour.onAdd(this, o));
        spotsToAdd.clear();

        //move handle
        handlePos += handleSpeed * currentRotation;

        if (handlePos > 360) handlePos -= 360;
        if (handlePos < 0) handlePos += 360;

        gracePeriod--;

        tickCount++;

        if (tickCount % 5 == 0 && gracePeriod < 0)
        {
            progress -= decay;
        }

        if (!isSettingsScreen())
        {

            if (progressSmooth < 0)
            {
                this.onClose();
            }

            if (progressSmooth > hp)
            {
                //if completed treasure minigame, or is a perfect catch with the mossy hook
                ClientPacketDistributor.sendToServer(new SCLSBFishingCompletedPayload(true));
                this.onClose();
            }
        }

        hitParticles.removeIf(HitFakeParticle::tick);
    }

    @Override
    public void onClose()
    {
        ClientPacketDistributor.sendToServer(new SCLSBFishingCompletedPayload(false));
        super.onClose();
    }

    public void addParticles(float posInDegrees, int count, int color)
    {
        int xPos = (int) (30 * Math.cos(Math.toRadians(posInDegrees - 90)));
        int yPos = (int) (30 * Math.sin(Math.toRadians(posInDegrees - 90)));

        for (int i = 0; i < count; i++)
        {
            hitParticles.add(
                    new HitFakeParticle(xPos, yPos,
                            new Vector2f(Utils.r.nextFloat() * 2 - 1, Utils.r.nextFloat() * 2 - 1),
                            color
                    ));
        }
    }

    public boolean isHoldingInput()
    {
        return isHoldingMouse || isHoldingKey;
    }

    public boolean isSettingsScreen()
    {
        return false;
    }

    public static int clampPos(int pos)
    {
        pos %= 360;
        if (pos < 0)
        {
            pos += 360;
        }
        return pos;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
