package com.evandev.reliable_backpacks.client.models;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BackpackModel {

    //BLOCK MODEL
    public static LayerDefinition createBlockLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -11.0F, -4.0F, 10.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition lid = base.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 19).addBox(-5.5F, -2.0F, -0.5F, 11.0F, 5.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(-9, 33).addBox(-5.5F, 1.0F, -0.5F, 11.0F, 0.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, -4.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    //PLAYER MODEL
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition base = partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(40, 0).addBox(-7.0F, -10.0F, -4.5F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.6F))
                .texOffs(0, 0).addBox(-7.0F, -9.0F, -1.0F, 8.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.0F, 2.5F));

        PartDefinition lid = base.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 15).addBox(-4.5F, -1.0F, -2.0F, 9.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(32, 18).addBox(-4.5F, 0.0F, -2.0F, 9.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -9.0F, 0.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
