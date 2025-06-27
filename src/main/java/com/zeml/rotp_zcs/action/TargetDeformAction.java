package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.TargetHitPart;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.power.impl.stand.StandUtil;
import com.github.standobyte.jojo.util.general.ObjectWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class TargetDeformAction extends StandEntityAction {

    public TargetDeformAction(StandEntityAction.Builder builder){
        super(builder);
    }


    @Override
    public void standTickRecovery(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if(!world.isClientSide){
            TargetHitPart hitPart = task.getAdditionalData().peekOrNull(TargetHitPart.class);
            if(hitPart == null){
                return;
            }
            if(task.getTarget().getType() ==  ActionTarget.TargetType.ENTITY){
                Entity entity = task.getTarget().getEntity();
                if (entity.isAlive() && entity instanceof LivingEntity){
                    LivingEntity targetEntity = StandUtil.getStandUser((LivingEntity) entity);
                    switch (hitPart){
                        case HEAD:
                            targetEntity.addEffect(new EffectInstance(Effects.BLINDNESS.getEffect(),30));
                            break;
                        case LEGS:
                            targetEntity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN,60,1));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ENTITY;
    }
}
