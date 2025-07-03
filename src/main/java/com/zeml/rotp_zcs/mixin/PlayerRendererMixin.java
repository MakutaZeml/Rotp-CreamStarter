package com.zeml.rotp_zcs.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {


    public PlayerRendererMixin(EntityRendererManager p_i50965_1_, PlayerModel<AbstractClientPlayerEntity> p_i50965_2_, float p_i50965_3_) {
        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
    }

    @Inject(method = "Lnet/minecraft/client/renderer/entity/PlayerRenderer;renderNameTag(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;Lnet/minecraft/util/text/ITextComponent;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", at = @At("HEAD"), cancellable = true)
    private void omRenderNameTag(AbstractClientPlayerEntity p_225629_1_, ITextComponent p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_, CallbackInfo ci){
        if(p_225629_1_.getItemBySlot(EquipmentSlotType.HEAD).getItem() instanceof MeatMaskItem){
            ItemStack itemStack = p_225629_1_.getItemBySlot(EquipmentSlotType.HEAD);
            MeatMaskItem maskItem = (MeatMaskItem) itemStack.getItem();
            if(maskItem.targetMode(itemStack)>0){
                ci.cancel();
                if(maskItem.targetMode(itemStack)==1){
                    // Almost The same
                    double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
                    p_225629_3_.pushPose();
                    if (d0 < 100.0D) {
                        Scoreboard scoreboard = p_225629_1_.getScoreboard();
                        ScoreObjective scoreobjective = scoreboard.getDisplayObjective(2);
                        if (scoreobjective != null) {
                            Score score = scoreboard.getOrCreatePlayerScore(p_225629_1_.getScoreboardName(), scoreobjective);
                            super.renderNameTag(p_225629_1_, (new StringTextComponent(Integer.toString(score.getScore()))).append(" ").append(scoreobjective.getDisplayName()), p_225629_3_, p_225629_4_, p_225629_5_);
                            p_225629_3_.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
                        }
                    }
                    GameProfile gameProfile = NBTUtil.readGameProfile(itemStack.getOrCreateTag());

                    ITextComponent textComponent = ITextComponent.nullToEmpty(gameProfile.getName());

                    super.renderNameTag(p_225629_1_, textComponent, p_225629_3_, p_225629_4_, p_225629_5_);
                    p_225629_3_.popPose();
                }
            }
        }
    }

    @Shadow public abstract ResourceLocation getTextureLocation(AbstractClientPlayerEntity p_110775_1_);

}
