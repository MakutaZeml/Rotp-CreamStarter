package com.zeml.rotp_zcs.client.render.entity.renderer.stand;

import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.client.render.entity.model.stand.StarterCreamModel;
import com.zeml.rotp_zcs.entity.stand.stands.CSEntity;
import com.github.standobyte.jojo.client.render.entity.renderer.stand.StandEntityRenderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class CreamStarterRenderer extends StandEntityRenderer<CSEntity, StarterCreamModel> {

    public CreamStarterRenderer(EntityRendererManager renderManager) {
        super(renderManager, new StarterCreamModel(), new ResourceLocation(CreamStarterAddon.MOD_ID, "textures/entity/stand/cream_starter.png"), 0);
    }

}
