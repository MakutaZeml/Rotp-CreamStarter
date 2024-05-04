package com.zeml.rotp_zcs.client.render.entity.renderer.damaging.projectile;

import com.github.standobyte.jojo.client.render.entity.renderer.SimpleEntityRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.client.render.entity.model.projectile.SprayModel;
import com.zeml.rotp_zcs.entity.damaging.projectile.SprayEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SprayRenderer extends SimpleEntityRenderer<SprayEntity,SprayModel> {

    public SprayRenderer(EntityRendererManager entityRendererManager) {
        super(entityRendererManager, new SprayModel(), new ResourceLocation(CreamStarterAddon.MOD_ID, "textures/entity/projectile/spray.png"));
    }


}
