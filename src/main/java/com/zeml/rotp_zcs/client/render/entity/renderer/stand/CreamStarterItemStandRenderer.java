package com.zeml.rotp_zcs.client.render.entity.renderer.stand;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.zeml.rotp_zcs.client.render.entity.model.stand.StarterCreamModel;
import com.zeml.rotp_zcs.entity.stand.stands.CSEntity;
import com.zeml.rotp_zcs.init.InitItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreamStarterItemStandRenderer extends LayerRenderer<CSEntity, StarterCreamModel> {


    public CreamStarterItemStandRenderer(IEntityRenderer<CSEntity, StarterCreamModel> p_i50926_1_) {
        super(p_i50926_1_);
    }

    @Override
    public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, CSEntity stand, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        p_225628_1_.pushPose();
        ItemStack itemstack = new ItemStack(InitItems.CREAM_STARTER.get());
        if(stand.getStandSkin().isPresent()){
            itemstack.getOrCreateTag().putString("standSkin", stand.getStandSkin().get().toString());
        }
        Minecraft.getInstance().getItemInHandRenderer().renderItem(stand, itemstack, ItemCameraTransforms.TransformType.GROUND, false, p_225628_1_, p_225628_2_, p_225628_3_);
        p_225628_1_.popPose();
    }
}