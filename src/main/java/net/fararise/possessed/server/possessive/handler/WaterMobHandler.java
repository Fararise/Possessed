package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;

public class WaterMobHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        if (player.isInWater()) {
            if (!player.onGround && player.motionY < 0.0) {
                player.motionY = 0.0;
            }
            if (player.isSneaking()) {
                player.motionY = -0.2;
            }
            player.moveRelative(player.moveStrafing, player.moveForward, 0.1F);
            player.moveEntity(player.motionX, player.motionY <= 0.0 ? player.motionY : Math.max(0.3, player.motionY), player.motionZ);
        } else if (player.onGround && player.getRNG().nextInt(20) == 0) {
            player.motionY = 0.4F;
            player.motionX += player.getRNG().nextFloat() - 0.5F;
            player.motionZ += player.getRNG().nextFloat() - 0.5F;
        }
    }

    @Override
    public void onDeath(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    @Override
    public void onClickBlock(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    @Override
    public void onClickAir(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityWaterMob.class;
    }
}
