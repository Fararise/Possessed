package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;

public class ChickenHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        if (!player.onGround && player.motionY < 0.0 && !player.isSneaking()) {
            player.motionY *= 0.6;
        }
        player.fallDistance = 0.0F;
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityChicken.class;
    }
}
