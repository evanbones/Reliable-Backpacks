package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.registry.*;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Backpacks {
    public static final String MODID = "reliable_backpacks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        BPBlocks.init();
        BPItems.init();
        BPBlockEntities.init();
        BPSounds.init();
        BPDataAttatchments.init();
    }
}