package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class WitherHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    @Override
    public boolean canPossess(EntityPlayer player, EntityLivingBase entity) {
        return false;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "wither");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityWither.class;
    }
}
