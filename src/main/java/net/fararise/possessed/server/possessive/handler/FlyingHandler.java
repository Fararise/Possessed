package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class FlyingHandler implements EntityPossessHandler {
    private Class<? extends EntityLivingBase> entity;

    public FlyingHandler(Class<? extends EntityLivingBase> entity) {
        this.entity = entity;
    }

    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        player.fallDistance = 0.0F;
        player.capabilities.allowFlying = true;
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return this.entity;
    }
}
