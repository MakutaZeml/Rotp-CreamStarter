package com.zeml.rotp_zcs.client.render.item.stand_skin;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zeml.rotp_zcs.CreamStarterAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class CreamStarterISTER extends ItemStackTileEntityRenderer {

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType,
                             MatrixStack matrixStack, IRenderTypeBuffer buffer, int light, int overlay){
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        IBakedModel pModel;
        pModel = ir.getModel(itemStack, null, null);
        RenderType rendertype = RenderType.cutout();

        CompoundNBT nbt = itemStack.getTag();

        if(nbt != null && nbt.contains("standSkin") && !nbt.getString("standSkin").isEmpty()){
            String path =  nbt.getString("standSkin").split(":")[1];

            String location = CreamStarterAddon.MOD_ID+":cream_starter_"+path;

            ModelResourceLocation modelResourceLocation = new ModelResourceLocation(location, "inventory");

            pModel = Minecraft.getInstance().getModelManager().getModel(modelResourceLocation);

            //CreamStarterAddon.LOGGER.debug("Resource {}, model {}", modelResourceLocation, pModel.getOverrides());
        }



        IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBufferDirect(
                buffer, rendertype, true, itemStack.hasFoil());
        ir.renderModelLists(pModel, itemStack, light, overlay, matrixStack, ivertexbuilder);
    }

}
