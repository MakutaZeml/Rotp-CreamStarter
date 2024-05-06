package com.zeml.rotp_zcs.init;

import com.zeml.rotp_zcs.CreamStarterAddon;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class IntTags {
    public static final Tags.IOptionalNamedTag<Item> EGGCELENT_MEAT = ItemTags.createOptional(new ResourceLocation(CreamStarterAddon.MOD_ID,"eggcelent_meat_source"));
    public static final Tags.IOptionalNamedTag<Item> GOOD_MEAT = ItemTags.createOptional(new ResourceLocation(CreamStarterAddon.MOD_ID,"good_meat_source"));
    public static final Tags.IOptionalNamedTag<Item> MID_MEAT = ItemTags.createOptional(new ResourceLocation(CreamStarterAddon.MOD_ID,"mid_meat_source"));
    public static final Tags.IOptionalNamedTag<Item> BAD_MEAT = ItemTags.createOptional(new ResourceLocation(CreamStarterAddon.MOD_ID,"bad_meat_source"));


    public static final Tags.IOptionalNamedTag<EntityType<?>> NO_MEATABLE = EntityTypeTags.createOptional(new ResourceLocation(CreamStarterAddon.MOD_ID,"no_meatable"));


    public static void iniTags(){}
}
