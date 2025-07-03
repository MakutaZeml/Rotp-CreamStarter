package com.zeml.rotp_zcs.client.render.entity.model;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;

public class CreamedModel<T extends LivingEntity> extends PlayerModel<T> {


    public CreamedModel(float inflate, boolean slim) {
        super(inflate, slim);
    }


}