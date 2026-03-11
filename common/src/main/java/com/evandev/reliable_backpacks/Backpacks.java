package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.registry.*;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Backpacks {
    public static void init() {
        ModConfig.load();
        BPSounds.init();
        BPBlocks.init();
        BPItems.init();
        BPBlockEntities.init();
        BPDataAttatchments.init();
    }
}