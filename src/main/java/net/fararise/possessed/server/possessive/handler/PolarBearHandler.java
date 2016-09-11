package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PolarBearHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntityPolarBear possessing = (EntityPolarBear) possessivePlayer.getPossessing();
        if (!player.worldObj.isRemote) {
            NBTTagCompound data = this.getData(player);
            short attackTimer = data.getShort("AttackTimer");
            if (attackTimer > 0) {
                possessing.setStanding(true);
                data.setShort("AttackTimer", (short) (attackTimer - 1));
            } else {
                if (player.isSwingInProgress) {
                    data.setShort("AttackTimer", (short) 15);
                }
                possessing.setStanding(false);
            }
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "polar_bear");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityPolarBear.class;
    }
}
