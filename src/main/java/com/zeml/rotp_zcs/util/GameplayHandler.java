package com.zeml.rotp_zcs.util;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.nonstand.type.vampirism.VampirismUtil;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.capability.LivingDataProvider;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitStands;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


@Mod.EventBusSubscriber(modid = CreamStarterAddon.MOD_ID)
public class GameplayHandler {


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerHurt(LivingHurtEvent event){
        LivingEntity living = event.getEntityLiving();
        Entity fro = event.getSource().getEntity();

        if(!living.level.isClientSide){
            // Stop hold
            if(event.getAmount()>10){
                if(living.isUsingItem() && living.getUseItem().getItem() == InitItems.CREAM_STARTER.get()){
                    living.stopUsingItem();
                }
                IStandPower.getStandPowerOptional(living).ifPresent(standPower -> {
                    if(standPower.getHeldAction() == InitStands.CS_HEAL.get()){
                        standPower.stopHeldAction(false);
                    }
                });
            }
            //Meat copy
            if(living.getItemBySlot(EquipmentSlotType.HEAD).getItem() == InitItems.MEAT_MASK.get()
                    && event.getSource().getDirectEntity() instanceof LivingEntity
            ){
                LivingEntity sourceEntity = (LivingEntity) event.getSource().getDirectEntity();
                ItemStack stack = living.getItemBySlot(EquipmentSlotType.HEAD);
                MeatMaskItem meatMaskItem = (MeatMaskItem) stack.getItem();
                if(meatMaskItem.getTargetType(stack).isEmpty()){
                    if(sourceEntity instanceof PlayerEntity){
                        CompoundNBT nbt = stack.getOrCreateTag();
                        NBTUtil.writeGameProfile(nbt,((PlayerEntity)sourceEntity).getGameProfile());
                        meatMaskItem.setTargetType(stack,sourceEntity.getName().getString(),1, Optional.of(sourceEntity.getUUID()));
                        meatMaskItem.setHostile(stack, JojoModUtil.isPlayerJojoVampiric((PlayerEntity) sourceEntity) && !JojoModConfig.getCommonConfigInstance(false).vampiresAggroMobs.get());
                    } else if (!(sourceEntity instanceof StandEntity)) {
                        meatMaskItem.setTargetType(stack,sourceEntity.getType().getRegistryName().toString(),2, Optional.empty());
                        meatMaskItem.setHostile(stack,sourceEntity.getType().getCategory() == EntityClassification.MONSTER);
                    }
                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;
        if(!player.level.isClientSide()){
            IStandPower.getStandPowerOptional(player).ifPresent(standPower -> {
                StandType<?> cs = InitStands.STAND_CREAM_STARTER.getStandType();
                if(standPower.getType() == cs){
                    if(standPower.getStandManifestation() instanceof StandEntity) {
                        if(!hasPlayerCS(player) && !isItemCS(player)){
                            ItemStack itemStack = new ItemStack(InitItems.CREAM_STARTER.get(),1);
                            CompoundNBT nbt = new CompoundNBT();
                            itemStack.setTag(nbt);

                            nbt.putString("owner",player.getName().getString());
                            nbt.putString("mode","attack");
                            nbt.putInt("Ammo", player.getCapability(LivingDataProvider.CAPABILITY).map(livingData -> livingData.getMeat()).orElse(CreamStarterItem.MAX_AMMO/2));

                            StandEntity stand = (StandEntity) standPower.getStandManifestation();
                            if(stand.getStandSkin().isPresent()){
                                nbt.putString("standSkin", stand.getStandSkin().get().toString());
                            }
                            player.addItem(itemStack);

                        }else {
                            if(hasPlayerCS(player)){
                                clearEntCS(player);
                            }
                            delDupCS(player);
                        }


                    }else {
                        clearCS(player);
                        clearEntCS(player);
                    }
                }else{
                    clearCS(player);
                    clearEntCS(player);
                }
            });
        }
    }


    /*
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEntityJoinWorld(EntityJoinWorldEvent event){
        Entity entity = event.getEntity();
        if (!entity.level.isClientSide() && entity instanceof MobEntity) {
            CreamStarterUtil.editMobAiGoals((MobEntity) entity);
        }
    }



     */


    private static void clearCS(PlayerEntity players){
        if(players instanceof ServerPlayerEntity){
            ServerPlayerEntity servPlater =  (ServerPlayerEntity) players;
            servPlater.getLevel().players().forEach(player -> {
                for (int i=0; i<player.inventory.getContainerSize();++i){
                    if(csOwner(players,player.inventory.getItem(i))){
                        player.inventory.getItem(i).shrink(1);
                    }
                }
            });
        }else {
            for (int i=0; i<players.inventory.getContainerSize();++i){
                if(csOwner(players,players.inventory.getItem(i))){
                    players.inventory.getItem(i).shrink(1);
                }
            }
        }
    }

    private static void clearEntCS(PlayerEntity player){
        if(player instanceof ServerPlayerEntity){
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.getLevel().getEntities().filter(entity -> entity instanceof ItemEntity && ((ItemEntity)entity).getItem().getItem() == InitItems.CREAM_STARTER.get())
                    .forEach(entity -> {
                        ItemStack itemStack = ((ItemEntity) entity).getItem();
                        if(csOwner(player,itemStack)){
                            entity.remove();
                        }
                    });
        }
    }


    private static void delDupCS(PlayerEntity players){
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) players;
        int count = 0;
        for (ServerPlayerEntity player : serverPlayer.getLevel().players()) {
            for (int i=0; i<player.inventory.getContainerSize();++i){
                ItemStack itemStack = player.inventory.getItem(i);
                if(csOwner(players,itemStack)){
                    count++;
                    if(count>1){
                        itemStack.shrink(1);
                    }
                }
            }
        }
    }


    private static boolean hasPlayerCS(PlayerEntity players){
        if(players instanceof ServerPlayerEntity){
            boolean turn = false;
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) players;
            for (ServerPlayerEntity player : serverPlayer.getLevel().players()) {
                for(int i=0;i<player.inventory.getContainerSize();++i){
                    if(csOwner(players, player.inventory.getItem(i))){
                        turn = true;
                        i= player.inventory.getContainerSize();
                    }
                }
            }
            return turn;
        }else {
            boolean turn = false;
            for(int i=0;i<players.inventory.getContainerSize();++i){
                turn =csOwner(players, players.inventory.getItem(i));
                i= players.inventory.getContainerSize();
            }
            return turn;
        }
    }

    private static boolean isItemCS(PlayerEntity players){
        if(players instanceof ServerPlayerEntity){
            ServerPlayerEntity player = (ServerPlayerEntity) players;
            boolean turn = player.getLevel().getEntities().filter(entity -> entity instanceof ItemEntity && ((ItemEntity)entity).getItem().getItem() == InitItems.CREAM_STARTER.get())
                    .anyMatch(entity -> {
                        ItemStack itemStack = ((ItemEntity) entity).getItem();
                        return csOwner(player, itemStack);
                    });

            return turn;
        }else {
            return players.level.getEntitiesOfClass(ItemEntity.class,players.getBoundingBox().inflate(1000), EntityPredicates.ENTITY_STILL_ALIVE).stream()
                    .anyMatch(itemEntity -> csOwner(players,itemEntity.getItem()));
        }
    }

    private static boolean csOwner(PlayerEntity player, ItemStack itemStack){
        if(itemStack.getItem() == InitItems.CREAM_STARTER.get()){
            CompoundNBT nbt = itemStack.getTag();
            if(nbt != null && nbt.contains("owner")){
                return nbt.getString("owner").equals(player.getName().getString());
            }
            return false;
        }
        return false;
    }
}
