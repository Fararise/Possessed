package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class BlazeHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NBTTagCompound data = this.getData(player);
            int projectileCooldown = data.getShort("ProjectileCooldown");
            if (projectileCooldown > 0) {
                projectileCooldown--;
                data.setShort("ProjectileCooldown", (short) projectileCooldown);
            }
        }
    }

    @Override
    public void onClickAir(PossessivePlayer possessivePlayer, EntityPlayer player) {
        if (!player.worldObj.isRemote && this.getData(player).getShort("ProjectileCooldown") <= 0) {
            player.worldObj.playEvent(null, 1018, new BlockPos((int) player.posX, (int) player.posY, (int) player.posZ), 0);
            float pitchVelocity = MathHelper.cos(player.rotationPitch * 0.017453292F);
            float velocityX = - MathHelper.sin(player.rotationYaw * 0.017453292F) * pitchVelocity;
            float velocityY = -MathHelper.sin(player.rotationPitch * 0.017453292F);
            float velocityZ = MathHelper.cos(player.rotationYaw * 0.017453292F) * pitchVelocity;
            EntitySmallFireball fireball = new EntitySmallFireball(player.worldObj, player, velocityX + player.motionX, velocityY + player.motionY, velocityZ + player.motionZ);
            fireball.posY = player.posY + player.height / 2.0F + 0.5D;
            player.worldObj.spawnEntityInWorld(fireball);
            this.getData(player).setShort("ProjectileCooldown", (short) 10);
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "blaze");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityBlaze.class;
    }
}
