package com.wdiscute.starcatcher_lite.registry;

import com.wdiscute.starcatcher_lite.StarcatcherLite;
import com.wdiscute.utils.MaybeStack;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public interface SCDataAttachments
{
    DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, StarcatcherLite.MOD_ID);


    Supplier<AttachmentType<Boolean>> IN_FISHING_MINIGAME = ATTACHMENT_TYPES.register(
            "in_fishing_minigame", () -> AttachmentType.builder(() -> false)
                    .build()
    );

    Supplier<AttachmentType<List<MaybeStack>>> ITEMS_BEING_FISHED = ATTACHMENT_TYPES.register(
            "items_being_fished", () -> AttachmentType.builder(() -> List.<MaybeStack>of())
                    .build()
    );

    // sets the value to default
    static <T> void remove(Entity holder, Supplier<AttachmentType<T>> attachmentType)
    {
        if(holder == null) return;
        holder.removeData(attachmentType);
    }

    // sets the value to default
    static <T> void remove(Entity holder, AttachmentType<T> attachmentType)
    {
        if(holder == null) return;
        holder.removeData(attachmentType);
    }

    static <T> void set(Entity holder, Supplier<AttachmentType<T>> attachmentType, T data)
    {
        if(holder == null) return;
        holder.setData(attachmentType, data);
    }

    static <T> void set(Entity holder, AttachmentType<T> attachmentType, T data)
    {
        if(holder == null) return;
        holder.setData(attachmentType, data);
    }

    static <T> T get(Entity holder, Supplier<AttachmentType<T>> attachmentType)
    {
        if(holder == null) throw new RuntimeException("Called Starcatcher DataAttachments Get() with a null entity");
        return holder.getData(attachmentType);
    }

    static <T> T get(Entity holder, AttachmentType<T> attachmentType)
    {
        if(holder == null) throw new RuntimeException("Called Starcatcher DataAttachments Get() with a null entity");
        return holder.getData(attachmentType);
    }

    static void register(IEventBus eventBus)
    {
        ATTACHMENT_TYPES.register(eventBus);
    }

}
