package com.evandev.reliable_backpacks.networking;

import com.evandev.reliable_backpacks.BackpackWearer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BackpackPayloadHandler {

    public static void HandleClientData(final BackpackOpenPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(payload.id());
            if (entity instanceof BackpackWearer backpackWearer) {
                if (payload.isOpen()) {
                    backpackWearer.onBackpackOpen();
                } else {
                    backpackWearer.onBackpackClose();
                }

            }
        });
    }
}
