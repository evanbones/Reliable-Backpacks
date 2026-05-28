package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.registry.*;

public class Backpacks {
    public static void init() {
        ModConfig.load();
        BPMenus.init();
        BPSounds.init();
        BPBlocks.init();
        BPItems.init();
        BPBlockEntities.init();
        BPDataAttatchments.init();
    }
}