package com.zeml.rotp_zcs.client.render.entity.renderer;

import com.github.standobyte.jojo.client.playeranim.PlayerAnimationHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.client.render.entity.model.CreamedModel;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import com.zeml.rotp_zcs.network.ModNetwork;
import com.zeml.rotp_zcs.network.client.QuitNBTRenderInfoPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class DisguiseLayer<T extends LivingEntity, M extends BipedModel<T>> extends LayerRenderer<T, M> {
    private boolean playerAnimHandled = false;

    public DisguiseLayer(IEntityRenderer<T, M> p_i50926_1_, M skinModel, boolean slim) {
        super(p_i50926_1_);

    }


    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick, float ticks, float yRot, float xRot) {
        if (entity.isInvisible()) {
            return;
        }
        if (!playerAnimHandled) {
            PlayerAnimationHandler.getPlayerAnimator().onArmorLayerInit(this);
            playerAnimHandled = true;
        }
        ItemStack stack = entity.getItemBySlot(EquipmentSlotType.HEAD);
        if(stack.getItem() instanceof MeatMaskItem){
            MeatMaskItem maskItem = (MeatMaskItem) stack.getItem();
            EntityRendererManager entityRendererManager = Minecraft.getInstance().getEntityRenderDispatcher();
            switch (maskItem.targetMode(stack)){
                case 1:
                    M normalModel = getParentModel();
                    if(maskItem.getTargetUUID(stack).isPresent()){
                        GameProfile profile = NBTUtil.readGameProfile(stack.getOrCreateTag());
                        Minecraft minecraft = Minecraft.getInstance();
                        if(profile != null){
                            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
                            RenderType renderType = map.containsKey(MinecraftProfileTexture.Type.SKIN) ?
                                    RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)) :
                                    RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.createPlayerUUID(maskItem.getTargetUUID(stack).toString())));
                            M skinModel = (M) new CreamedModel<>(0,isSlimSkin(profile));
                            normalModel.copyPropertiesTo(skinModel);
                            skinModel.prepareMobModel(entity,limbSwing,limbSwingAmount,packedLight);
                            skinModel.setupAnim(entity,limbSwing,limbSwingAmount,ticks,yRot,xRot);
                            IVertexBuilder vertexBuilder = buffer.getBuffer(renderType);
                            skinModel.renderToBuffer(matrixStack,vertexBuilder,packedLight,LivingRenderer.getOverlayCoords(entity, 0.0F),1F,1F,1F,1F);
                        }else {
                            ModNetwork.sendToServer(new QuitNBTRenderInfoPacket());
                        }
                    }
                    break;
                case 2:
                    Optional<EntityType<?>> OptionalType = ForgeRegistries.ENTITIES.getValues().stream().filter(entityType -> entityType.getRegistryName().toString().equals(maskItem.getTargetType(stack))).findAny();
                    if(OptionalType.isPresent()){
                        EntityRenderer<T> entityRenderer = (EntityRenderer<T>) entityRendererManager.renderers.get(OptionalType.get());
                        EntityRenderer<T> creamRenderer = (EntityRenderer<T>) entityRendererManager.getRenderer(entity);
                        if(entityRenderer instanceof BipedRenderer && creamRenderer instanceof PlayerRenderer){
                            T fictional = (T) OptionalType.get().create(entity.level);
                            fictional.kill();
                            M model = (M) ((BipedRenderer<?, ?>) entityRenderer).getModel();
                            M disguiseModel = (M) ((PlayerRenderer) creamRenderer).getModel();
                            model.young = false;
                            model.setupAnim(fictional,limbSwing,limbSwingAmount,ticks,yRot,xRot);
                            if(!armorLess(entity)){
                                model.leftArm.copyFrom(disguiseModel.leftArm);
                                model.rightArm.copyFrom(disguiseModel.rightArm);
                                model.head.copyFrom(disguiseModel.head);
                                model.leftLeg.copyFrom(disguiseModel.leftLeg);
                                model.rightLeg.copyFrom(disguiseModel.rightLeg);
                                model.body.copyFrom(disguiseModel.body);
                            }
                            ResourceLocation location = entityRenderer.getTextureLocation(fictional);
                            IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(location));
                            model.renderToBuffer(matrixStack,vertexBuilder,packedLight,LivingRenderer.getOverlayCoords(entity, 0.0F),1F,1F,1F,1F);
                        }else {
                            ModNetwork.sendToServer(new QuitNBTRenderInfoPacket());
                        }
                    }else {
                        ModNetwork.sendToServer(new QuitNBTRenderInfoPacket());
                    }
                    break;
            }

        }
    }


    private boolean armorLess(T entity){
        return entity.getItemBySlot(EquipmentSlotType.CHEST).isEmpty() && entity.getItemBySlot(EquipmentSlotType.LEGS).isEmpty() &&
                entity.getItemBySlot(EquipmentSlotType.FEET).isEmpty() && entity.getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty() &&
                entity.getItemBySlot(EquipmentSlotType.OFFHAND).isEmpty();
    }



    private static final JsonParser PARSER = new JsonParser();
    public boolean isSlimSkin(GameProfile profile) {
        Property textures = profile.getProperties().get("textures").stream().findFirst().orElse(null);
        if (textures == null) {
            return DefaultPlayerSkin.getSkinModelName(profile.getId()).equals("slim");
        }
        try {
            String json = new String(Base64.getDecoder().decode(textures.getValue()));
            JsonObject jsonObject = PARSER.parse(json).getAsJsonObject();
            if (jsonObject.has("textures")) {
                JsonObject texturesObj = jsonObject.getAsJsonObject("textures");
                if (texturesObj.has("SKIN")) {
                    JsonObject skin = texturesObj.getAsJsonObject("SKIN");
                    if (skin.has("metadata")) {
                        JsonObject metadata = skin.getAsJsonObject("metadata");
                        if (metadata.has("model")) {
                            return metadata.get("model").getAsString().equals("slim");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
