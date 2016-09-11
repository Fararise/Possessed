package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class IronGolemHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntityIronGolem possessing = (EntityIronGolem) possessivePlayer.getPossessing();
        if (possessing.getAttackTimer() <= 0 && player.isSwingInProgress) {
            possessing.handleStatusUpdate((byte) 4);
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "iron_golem");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityIronGolem.class;
    }
}
