package com.zeml.rotp_zcs.client;

import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreamStarterAddon.MOD_ID,value = Dist.CLIENT)
public class ClientHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event){
        PlayerEntity player = event.getPlayer();
        PlayerModel<AbstractClientPlayerEntity> model = event.getRenderer().getModel();
        ItemStack itemStack = player.getItemBySlot(EquipmentSlotType.HEAD);
        if(itemStack.getItem() instanceof MeatMaskItem){
            MeatMaskItem meatMaskItem = (MeatMaskItem) itemStack.getItem();
            if(meatMaskItem.targetMode(itemStack) > 0){
                model.setAllVisible(false);

            }
        }

    }



    /*
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onNameDisplay(PlayerEvent.NameFormat event){
        PlayerEntity player = event.getPlayer();
        if((player.getItemBySlot(EquipmentSlotType.HEAD).getItem() instanceof MeatMaskItem)){
            ItemStack stack = player.getItemBySlot(EquipmentSlotType.HEAD);
            MeatMaskItem maskItem = (MeatMaskItem) stack.getItem();
            ITextComponent iTextComponent;
            switch (maskItem.targetMode(stack)){
                case 1:
                    iTextComponent = new StringTextComponent(maskItem.getTargetType(stack));
                    event.setDisplayname(iTextComponent);
                    break;
                case 2:
                    iTextComponent = ITextComponent.nullToEmpty("");
                    event.setDisplayname(iTextComponent);
                    break;
                default:
                    break;
            }
        }
    }
     */

}
