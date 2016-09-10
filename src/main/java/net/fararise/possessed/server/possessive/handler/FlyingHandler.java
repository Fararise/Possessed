package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class FlyingHandler implements EntityPossessHandler {
    private Class<? extends EntityLivingBase> entity;
    private ResourceLocation identifier;

    public FlyingHandler(Class<? extends EntityLivingBase> entity, ResourceLocation identifier) {
        this.entity = entity;
        this.identifier = identifier;
    }

    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        player.fallDistance = 0.0F;
        player.capabilities.allowFlying = true;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return this.entity;
    }
}
