package com.wdiscute.starcatcher_lite.minigame;

import com.wdiscute.utils.ScreenUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

import java.util.Random;

public class HitFakeParticle
{
    private static final Random random = new Random();

    public int color;

    public Vector2f pos;
    public double speed;
    public Vector2f vecDirection;
    public int lifetime;
    public int maxLifetime;

    public void render(GuiGraphicsExtractor guiGraphics, int width, int height)
    {
        Matrix3x2fStack poseStack = guiGraphics.pose();
        poseStack.pushMatrix();
        poseStack.translate(pos.x, pos.y);

        ScreenUtils.fill(guiGraphics, width / 2, height / 2, 1, 1, color);

        poseStack.popMatrix();
    }

    public HitFakeParticle(int x, int y, Vector2f vec, int color)
    {
        this.color = color;

        pos = new Vector2f(x + random.nextFloat() * 10 - 5, y + random.nextFloat() * 10 - 5);
        this.vecDirection = vec.normalize();
        this.speed = 0.2 + random.nextFloat() / 3;
        this.maxLifetime = (int) (5 + random.nextFloat() * 20);

        if(random.nextFloat() > 0.9) this.maxLifetime += (int) (40 + random.nextFloat() * 30);
    }

    public boolean tick()
    {
        lifetime++;

        pos = pos.add(new Vector2f((float) (vecDirection.x * speed), (float) (vecDirection.y * speed)));

        return lifetime > maxLifetime;
    }
}
