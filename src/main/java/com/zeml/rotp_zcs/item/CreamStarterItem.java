package com.zeml.rotp_zcs.item;

import com.github.standobyte.jojo.entity.damaging.projectile.HamonBubbleEntity;
import com.zeml.rotp_zcs.entity.damaging.projectile.HealSprayEntity;
import com.zeml.rotp_zcs.entity.damaging.projectile.SprayEntity;
import com.zeml.rotp_zcs.init.InitSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class CreamStarterItem extends Item {
    public CreamStarterItem(Properties properties) {
        super(properties);
    }


    @Override
    public void onUseTick(World world, LivingEntity entity, ItemStack stack, int remainingTicks){
        boolean shot = stack.getTag().getString("mode").equals("attack");
        if(!world.isClientSide){

            if(remainingTicks <=1){
                entity.releaseUsingItem();
                return;
            }
            if(shot){
                SprayEntity spray = new SprayEntity(entity, world);
                spray.shootFromRotation(entity,5,0.5F);
                world.addFreshEntity(spray);
            }else {
                HealSprayEntity spray = new HealSprayEntity(entity, world);
                spray.shootFromRotation(entity,5,0.5F);
                world.addFreshEntity(spray);
            }
            world.playSound(null,entity.blockPosition(), InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1,1);

        }
    }




    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getItemInHand(hand);
        player.startUsingItem(hand);

        return ActionResult.consume(handStack);

    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int remainingTicks){
        if(stack.hasTag() || stack.getTag().contains("ticks")){
            stack.getTag().putInt("ticks",0);
        }
        if(entity instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) entity;
            player.getCooldowns().addCooldown(this,20);
        }
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }
}
