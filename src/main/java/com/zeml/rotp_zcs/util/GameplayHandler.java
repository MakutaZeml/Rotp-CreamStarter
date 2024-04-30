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
import net.minecraft.util.Hand;
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
        if(!player.level.isClientSide){
            IStandPower.getStandPowerOptional(player).ifPresent(standPower -> {
                StandType<?> cs = InitStands.STAND_CREAM_STARTER.getStandType();
                if(standPower.getType() == cs){
                    if(standPower.getStandManifestation() instanceof StandEntity){
                        /* Giving only one Cream Starter */
                        boolean give = true;
                        if(player instanceof ServerPlayerEntity){
                            give = servPlayer(player) && servEnt(player);
                        }
                        if(give){
                            ItemStack itemStack = new ItemStack(InitItems.CREAM_STARTER.get(),1);
                            CompoundNBT nbt = new CompoundNBT();
                            itemStack.setTag(nbt);

                            nbt.putString("owner",player.getName().getString());
                            nbt.putBoolean("attack",true);

                            player.addItem(itemStack);

                        }
                    }else {
                        csInv(player);
                        csEnt(player);
                    }
                }
            });
        }
    }




    private static void csInv(PlayerEntity player){
        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack inventoryStack = player.inventory.getItem(i);
            if (inventoryStack.getItem() == InitItems.CREAM_STARTER.get()) {
                inventoryStack.shrink(inventoryStack.getCount());
            }
        }
    }

    private static void csEnt(PlayerEntity player){
        if(player instanceof ServerPlayerEntity){
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.getLevel().getEntities().filter(entity -> entity instanceof ItemEntity && ((ItemEntity)entity).getItem().getItem() == InitItems.CREAM_STARTER.get())
                    .forEach(entity -> {
                        ItemStack itemStack = ((ItemEntity) entity).getItem();
                        if(!itemCheck(player,itemStack)){
                            entity.remove();
                        }
                    });
        }
    }

    public static boolean servPlayer(PlayerEntity player){
        if(player instanceof ServerPlayerEntity){
            AtomicBoolean turn = new AtomicBoolean(false);
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            serverPlayer.getLevel().players().forEach(serverPlayerEntity -> {
                for(int i=0;i<serverPlayerEntity.inventory.getContainerSize(); ++i){
                    ItemStack itemStack = serverPlayerEntity.inventory.getItem(i);
                   turn.set(itemCheck(player, itemStack));
                   i=player.inventory.getContainerSize();
                }
                if(turn.get()){
                    turn.set(itemCheck(player,player.getItemInHand(Hand.OFF_HAND)));
                }
            });
            return turn.get();
        }
        return true;
    }


    public static boolean servEnt(PlayerEntity player){
        if(player instanceof ServerPlayerEntity){

            AtomicBoolean turn = new AtomicBoolean(true);
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

            serverPlayer.getLevel().getEntities().filter(entity -> entity instanceof ItemEntity && ((ItemEntity)entity).getItem().getItem() == InitItems.CREAM_STARTER.get())
                    .forEach(entity -> {
                ItemStack itemStack = ((ItemEntity) entity).getItem();
                turn.set(itemCheck(player, itemStack));
            });

            return turn.get();

        }
        return true;
    }

    public static boolean hasempty(PlayerEntity player){
        boolean turn = false;
        for(int i=1;i<player.inventory.getContainerSize();++i){
            if (player.inventory.getItem(i).isEmpty()){
                turn = true;
                i=player.inventory.getContainerSize();
            }
        }
        return turn;
    }


    public static boolean itemCheck(PlayerEntity player, ItemStack stack){
        if (stack.getItem()==InitItems.CREAM_STARTER.get()){
            CompoundNBT nbt = stack.getTag();
            if(nbt != null && nbt.contains("owner")){
                return !nbt.getString("owner").equals(player.getName().getString());
            }
        }
        return true;
    }

}
