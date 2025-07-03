package com.zeml.rotp_zcs.network.client;

import com.github.standobyte.jojo.network.packets.IModPacketHandler;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class QuitNBTRenderInfoPacket {

    public QuitNBTRenderInfoPacket(){
    }

    public static class Handler implements IModPacketHandler<QuitNBTRenderInfoPacket> {

        @Override
        public void encode(QuitNBTRenderInfoPacket packet, PacketBuffer buf) {

        }

        @Override
        public QuitNBTRenderInfoPacket decode(PacketBuffer buf) {
            return new QuitNBTRenderInfoPacket();
        }

        @Override
        public void handle(QuitNBTRenderInfoPacket QuitNBTRenderInfoPacket, Supplier<NetworkEvent.Context> ctx) {

            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                ServerPlayerEntity player = context.getSender();
                if (player != null) {
                    ItemStack itemStack = player.getItemBySlot(EquipmentSlotType.HEAD);
                    if(itemStack.getItem() instanceof MeatMaskItem){
                        MeatMaskItem meatMaskItem = (MeatMaskItem) itemStack.getItem();
                        meatMaskItem.setTargetType(itemStack,"",0, Optional.empty());
                    }

                } else {
                    System.out.println("Player es null");
                }
            });
            context.setPacketHandled(true);
        }

        @Override
        public Class<QuitNBTRenderInfoPacket> getPacketClass() {
            return QuitNBTRenderInfoPacket.class;
        }
    }
}
