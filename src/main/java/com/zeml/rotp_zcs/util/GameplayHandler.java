package com.zeml.rotp_zcs.util;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitStands;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;


@Mod.EventBusSubscriber(modid = CreamStarterAddon.MOD_ID)
public class GameplayHandler {


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerHurt(LivingHurtEvent event){
        LivingEntity living = event.getEntityLiving();
        Entity fro = event.getSource().getEntity();

        if(!living.level.isClientSide){
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
                            nbt.putInt("Ammo", (int) Math.round(Math.random()* CreamStarterItem.MAX_AMMO/2));


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
