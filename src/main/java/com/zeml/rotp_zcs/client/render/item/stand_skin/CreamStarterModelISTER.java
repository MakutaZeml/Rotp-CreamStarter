package com.zeml.rotp_zcs.client.render.item.stand_skin;

import com.github.standobyte.jojo.client.render.item.generic.ItemISTERModelWrapper;
import com.github.standobyte.jojo.util.mc.reflection.ClientReflection;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SuppressWarnings("deprecation")
public class CreamStarterModelISTER implements IBakedModel {
    private IBakedModel existingModel;
    private CreamStarterOverrideList csOverrides;


    public CreamStarterModelISTER(IBakedModel existingModel) {
        this.existingModel = existingModel;
        this.csOverrides = new CreamStarterOverrideList(existingModel.getOverrides());
    }

    public CreamStarterModelISTER refreshOverrides(Map<ResourceLocation, IBakedModel> registry){
        ItemOverrideList overridesList = existingModel.getOverrides();
        if (overridesList != null){
            List<ItemOverride> overrides = ClientReflection.getOverrides(overridesList);
            if (!overrides.isEmpty()){
                List<IBakedModel> overrideModels = ClientReflection.getOverrideModels(overridesList);
                for (int i = 0; i < overrides.size(); i++) {
                    ItemOverride override = overrides.get(i);
                    ResourceLocation key = override.getModel();
                    key = new ModelResourceLocation(new ResourceLocation(key.getNamespace(), key.getPath().replace("item/", "")), "inventory");
                    IBakedModel replacementModel = registry.get(key);
                    if (replacementModel != null) {
                        overrideModels.set(i, replacementModel);
                    }
                }
            }
        }
        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable  BlockState state, @Nullable Direction direction, Random rand) {
        return this.existingModel.getQuads(state, direction, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.existingModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return this.existingModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.existingModel.getParticleIcon();
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return this.existingModel.getTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return csOverrides;
    }



}
