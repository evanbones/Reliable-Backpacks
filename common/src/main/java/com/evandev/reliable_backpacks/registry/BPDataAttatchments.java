package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.BackpackWearer;
import net.minecraft.world.entity.LivingEntity;

public class BPDataAttatchments {

    public static void init() {
    }

    public static int getOpenCount(LivingEntity entity) {
        if (entity instanceof BackpackWearer wearer) {
            return wearer.getOpenCount();
        }
        return 0;
    }

    public static int getOpenTicks(LivingEntity entity) {
        if (entity instanceof BackpackWearer wearer) {
            return wearer.getOpenTicks();
        }
        return 0;
    }
}