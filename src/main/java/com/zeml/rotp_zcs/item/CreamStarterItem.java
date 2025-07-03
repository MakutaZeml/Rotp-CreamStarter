package com.zeml.rotp_zcs.item;

import com.github.standobyte.jojo.entity.damaging.projectile.HamonBubbleEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zcs.capability.LivingData;
import com.zeml.rotp_zcs.capability.LivingDataProvider;
import com.zeml.rotp_zcs.entity.damaging.projectile.HealSprayEntity;
import com.zeml.rotp_zcs.entity.damaging.projectile.SprayEntity;
import com.zeml.rotp_zcs.init.InitSounds;
import com.zeml.rotp_zcs.init.IntTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CreamStarterItem extends Item {
    public CreamStarterItem(Properties properties) {
        super(properties);
    }


    @Override
    public void onUseTick(World world, LivingEntity entity, ItemStack stack, int remainingTicks){
        boolean shot = stack.getTag().getString("mode").equals("attack");
        if(!world.isClientSide){
            if(remainingTicks <=1 || stack.getTag().getInt("Ammo")<=0){
                entity.releaseUsingItem();
                return;
            }
                if(shot){
                    RayTraceResult[] rayTraceResults = JojoModUtil.rayTraceMultipleEntities(entity,4, target ->target.isAlive() && !IntTags.NO_MEATABLE.contains(target.getType()) ,.5,0);
                    for (RayTraceResult rayTraceResult:rayTraceResults){
                        if(rayTraceResult.getType() == RayTraceResult.Type.ENTITY){
                            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity ){
                                LivingEntity living = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                            }

                        }
                    }

                    SprayEntity spray = new SprayEntity(entity, world);
                    spray.shootFromRotation(entity,5,0.5F);
                    spray.setOwner(entity);
                    world.addFreshEntity(spray);

                }else {
                    HealSprayEntity spray = new HealSprayEntity(entity, world);
                    spray.shootFromRotation(entity,5,0.5F);
                    world.addFreshEntity(spray);

                }
                consumeAmmo(stack);
                world.playSound(null,entity.blockPosition(), InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1,1);
                if(stack.getTag().getInt("Ammo")==0){

                }

        }
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        if(stack.getTag() != null){
            boolean shot = stack.getTag().getString("mode").equals("attack");
            TranslationTextComponent mode = new TranslationTextComponent(shot?"rotp_zcs.mode.attack":"rotp_zcs.mode.heal");
            return new TranslationTextComponent(this.getDescriptionId(stack),mode);
        }
        return new TranslationTextComponent(this.getDescriptionId(stack));
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
        entity.getCapability(LivingDataProvider.CAPABILITY).ifPresent(livingData -> livingData.setMeat(getMeat(stack)));
    }


    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    public static final int MAX_AMMO = 600;

    private boolean consumeAmmo(ItemStack cream) {
        int ammo = getAmmo(cream);
        if (ammo < 0) {
            cream.getTag().putInt("Ammo", 0);
            return false;
        }
        if (ammo > 0) {
            cream.getTag().putInt("Ammo", --ammo);
            return true;
        }
        return false;
    }

    private int getMeat(ItemStack gun) {
        return gun.getOrCreateTag().getInt("Ammo");
    }

    private static int getAmmo(ItemStack gun) {
        return gun.getOrCreateTag().getInt("Ammo");
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity user, LivingEntity target) {
        if(!IntTags.NO_MEATABLE.contains(target.getType()) && stack.hasTag() && stack.getTag().getInt("Ammmo")<MAX_AMMO){
            int fill = stack.getTag().getInt("Ammo")+ 2*Math.round(target.getHealth());
            stack.getTag().putInt("Ammo",fill);
        }
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return getAmmo(stack) < MAX_AMMO;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1 - ((double) getAmmo(stack) / (double) MAX_AMMO);
    }


    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }


}
