package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;

public class SquidHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntitySquid squid = (EntitySquid) possessivePlayer.getPossessing();
        if (squid.squidRotation > Math.PI * 2.0) {
            if (squid.worldObj.isRemote) {
                squid.squidRotation = 0.0F;
            }
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
        return EntitySquid.class;
    }
}
