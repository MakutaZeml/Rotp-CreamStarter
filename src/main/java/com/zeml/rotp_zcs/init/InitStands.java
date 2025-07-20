package com.zeml.rotp_zcs.init;

import com.github.standobyte.jojo.action.stand.*;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.action.*;
import com.zeml.rotp_zcs.action.DeformTargetAction;
import com.zeml.rotp_zcs.entity.stand.stands.CSEntity;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), CreamStarterAddon.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), CreamStarterAddon.MOD_ID);

    // ======================================== Cream Starter ========================================


    public static final RegistryObject<StandEntityAction> CS_GIVE = ACTIONS.register("give_cs",
            ()->new GiveCreamStarter(new StandEntityAction.Builder().staminaCost(1)));

    public static final RegistryObject<StandEntityAction> CS_HEAL = ACTIONS.register("cs_heal",
            ()->new Healing(new StandEntityAction.Builder().staminaCostTick(10).cooldown(20).holdType(60)
                    .resolveLevelToUnlock(3).standUserWalkSpeed(1)));

    public static final RegistryObject<StandEntityAction> CS_CHANGE = ACTIONS.register("cs_switch",
            ()->new ChangeMode(new StandEntityAction.Builder().cooldown(10)
                    .standUserWalkSpeed(1).standSound(InitSounds.CS_SHAKE)
                    .resolveLevelToUnlock(2)));

    public static final RegistryObject<StandEntityAction> CS_ITEM_FILL = ACTIONS.register("cs_item_fill",
            ()->new FillMeatItems(new StandEntityAction.Builder().holdType().standSound(InitSounds.CS_REFILL)));

    public static final RegistryObject<StandEntityAction> CS_ENTITY_FILL = ACTIONS.register("cs_entity_fill",
            ()->new FillingEntities(new StandEntityAction.Builder().resolveLevelToUnlock(1).holdType().standSound(InitSounds.CS_REFILL)
                    .shiftVariationOf(CS_ITEM_FILL)));


    public static final RegistryObject<StandEntityAction> CS_DEFORM = ACTIONS.register("cs_deform", ()->
            new DeformAction(new StandEntityAction.Builder().resolveLevelToUnlock(3).holdToFire(15,false)
            ));

    public static final RegistryObject<StandEntityAction> CS_DEFORM_TARGET = ACTIONS.register("cs_def", ()->
            new DeformTargetAction(new StandEntityAction.Builder().resolveLevelToUnlock(3)
                    .holdToFire(10,false).swingHand()
            ));
    public static final RegistryObject<StandEntityAction> CS_DISGUISE_ITEM = ACTIONS.register("cs_dsg_item", ()->
            new DisguiseItemAction(new StandEntityAction.Builder().resolveLevelToUnlock(2).holdToFire(15,false)
            ));


    public static final RegistryObject<StandEntityAction> PUT_OBJET = ACTIONS.register("cs_item", ()->
            new PutItemAction(new StandEntityAction.Builder().resolveLevelToUnlock(4).holdToFire(15,false)
                    .staminaCost(100)
            ));

    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<CSEntity>> STAND_CREAM_STARTER =
            new EntityStandRegistryObject<>("cream_starter",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                            .color(0xF6F095)
                            .storyPartName(ModStandsInit.PART_7_NAME)
                            .leftClickHotbar(
                                    CS_ITEM_FILL.get(),
                                    CS_CHANGE.get(),
                                    CS_HEAL.get(),
                                    CS_DEFORM.get(),
                                    CS_DISGUISE_ITEM.get()
                            )
                            .rightClickHotbar(
                                    CS_GIVE.get(),
                                    PUT_OBJET.get(),
                                    CS_DEFORM_TARGET.get()
                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .tier(2)
                                    .power(6.0)
                                    .speed(10.0)
                                    .range(9.0)
                                    .durability(16.0)
                                    .precision(2.0)
                                    .randomWeight(2)
                            )
                            .addOst(InitSounds.HOT_OST)
                            .disableManualControl().disableStandLeap()
                            .addSummonShout(InitSounds.USER_CS)
                            .build(),

                    InitEntities.ENTITIES,
                    () -> new StandEntityType<CSEntity>(CSEntity::new, 1F, 1F)
                            .summonSound(InitSounds.CS_SUMMON)
                            .unsummonSound(InitSounds.CS_UNSUMMON))
                    .withDefaultStandAttributes();
}
