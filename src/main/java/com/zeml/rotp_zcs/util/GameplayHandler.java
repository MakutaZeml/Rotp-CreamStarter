package com.zeml.rotp_zcs.util;

import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitStands;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicBoolean;


@Mod.EventBusSubscriber(modid = CreamStarterAddon.MOD_ID)
public class GameplayHandler {

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
                            nbt.putBoolean("attack",true);

                            player.addItem(itemStack);

                        }else {

                        }
                    }else {
                        clearCS(player);
                        clearEntCS(player);
                    }
                }
            });
        }
    }



    private static void clearCS(PlayerEntity players){
        if(players instanceof ServerPlayerEntity){
            ServerPlayerEntity servPlater =  (ServerPlayerEntity) players;
            servPlater.getLevel().players().forEach(player -> {
                for (int i=0; i<player.inventory.getContainerSize();++i){
                    if(csOwner(player,player.inventory.getItem(i))){
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



    private static boolean hasPlayerCS(PlayerEntity players){
        if(players instanceof ServerPlayerEntity){
            AtomicBoolean turn = new AtomicBoolean(false);
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) players;
            serverPlayer.getLevel().players().forEach(player -> {
                for(int i=0;i<player.inventory.getContainerSize();++i){
                    if(csOwner(player, player.inventory.getItem(i))){
                        turn.set(true);
                        i= player.inventory.getContainerSize();
                    }

                }
            });
            return turn.get();
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
            AtomicBoolean turn = new AtomicBoolean(false);
            ServerPlayerEntity player = (ServerPlayerEntity) players;
            player.getLevel().getEntities().filter(entity -> entity instanceof ItemEntity && ((ItemEntity)entity).getItem().getItem() == InitItems.CREAM_STARTER.get())
                    .forEach(entity -> {
                        ItemStack itemStack = ((ItemEntity) entity).getItem();
                        turn.set(csOwner(player, itemStack));
                    });
            return turn.get();
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
