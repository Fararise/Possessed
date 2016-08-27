package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;

public class SlimeHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        double speedX = player.posX - player.prevPosX;
        double speedZ = player.posZ - player.prevPosZ;
        double speed = speedX * speedX + speedZ * speedZ;
        if (player.onGround && (speed > 0.01 || speed < -0.01)) {
            player.jump();
        }
    }

    @Override
    public void onJump(PossessivePlayer possessivePlayer, EntityPlayer player) {
        player.motionY += 0.09;
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySlime.class;
    }
}
