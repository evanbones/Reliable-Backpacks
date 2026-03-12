package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class BPLayers {

    public static final ModelLayerLocation BACKPACK = getLocation("backpack");
    public static final ModelLayerLocation BACKPACK_BLOCK = getLocation("backpack_block");

    public static final ModelLayerLocation OTHER_BACKPACK = getLocation("other_backpack");
    public static final ModelLayerLocation OTHER_BACKPACK_BLOCK = getLocation("other_backpack_block");

    private static ModelLayerLocation getLocation(String name) {
        return new ModelLayerLocation(new ResourceLocation(Constants.MOD_ID, name), "main");
    }
}