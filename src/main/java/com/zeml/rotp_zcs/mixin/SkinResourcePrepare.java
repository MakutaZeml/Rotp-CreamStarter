package com.zeml.rotp_zcs.mixin;

import com.github.standobyte.jojo.client.resources.models.StandModelOverrides;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.zeml.rotp_zcs.client.render.item.stand_skin.CreamStarterOverrideList;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = StandSkinsManager.SkinResourcePrepare.class, remap = false)
public class SkinResourcePrepare {

    @Shadow
    List<Pair<ResourceLocation, StandModelOverrides.CustomModelPrepared>> customModels;

    @Inject(method = "apply", at=@At("TAIL"))
    private void clApply(StandSkin skin, CallbackInfo ci){
        if(skin.standTypeId.getPath().equals("cream_starter")){
            CreamStarterOverrideList.onGettingStandSkins(skin);
        }
    }



}
