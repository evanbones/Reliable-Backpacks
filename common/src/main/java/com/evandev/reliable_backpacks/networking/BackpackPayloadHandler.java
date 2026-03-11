package com.evandev.reliable_backpacks.networking;

import com.evandev.reliable_backpacks.BackpackWearer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class BackpackPayloadHandler {

    public static void handleClientData(final BackpackOpenPayload payload, Player player) {
        if (player == null) return;

        Entity entity = player.level().getEntity(payload.id());
        if (entity instanceof BackpackWearer backpackWearer) {
            if (payload.isOpen()) {
                backpackWearer.onBackpackOpen();
            } else {
                backpackWearer.onBackpackClose();
            }
        }
    }
}