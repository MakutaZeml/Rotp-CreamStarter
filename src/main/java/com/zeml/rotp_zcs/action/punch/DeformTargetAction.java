package com.zeml.rotp_zcs.action.punch;

import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import net.minecraft.entity.Entity;

public class DeformTargetAction extends StandEntityHeavyAttack {

    public DeformTargetAction(StandEntityHeavyAttack.Builder builder){
        super(builder);
    }


    @Override
    public StandEntityPunch punchEntity(StandEntity stand, Entity target, StandEntityDamageSource dmgSource) {
        return super.punchEntity(stand, target, dmgSource).addKnockback(0).damage(0);
    }


    @Override
    public int getStandWindupTicks(IStandPower standPower, StandEntity standEntity) {
        return 0;
    }
}
