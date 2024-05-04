package com.zeml.rotp_zcs.client.render.entity.renderer.damaging.projectile;

import com.github.standobyte.jojo.client.render.entity.renderer.SimpleEntityRenderer;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.client.render.entity.model.projectile.SprayHealModel;
import com.zeml.rotp_zcs.entity.damaging.projectile.HealSprayEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class SprayHealRenderer extends SimpleEntityRenderer<HealSprayEntity, SprayHealModel> {

    public SprayHealRenderer(EntityRendererManager entityRendererManager) {
        super(entityRendererManager, new SprayHealModel(), new ResourceLocation(CreamStarterAddon.MOD_ID, "textures/entity/projectile/spray.png"));
    }


}