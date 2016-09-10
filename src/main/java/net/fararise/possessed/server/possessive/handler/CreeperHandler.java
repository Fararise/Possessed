package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CreeperHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
    }

    @Override
    public void onClickAir(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntityCreeper possessing = (EntityCreeper) possessivePlayer.getPossessing();
        possessing.ignite();
        possessing.setDead();
        PossessHandler.possess(player, null);
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "creeper");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityCreeper.class;
    }
}
