package com.zeml.rotp_zcs.init;

import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreamStarterAddon.MOD_ID);


    public static final RegistryObject<CreamStarterItem> CREAM_STARTER = ITEMS.register("cream_starter",
            ()->new CreamStarterItem(new Item.Properties().stacksTo(1)));

}
