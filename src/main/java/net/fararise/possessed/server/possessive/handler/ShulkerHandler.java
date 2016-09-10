package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class ShulkerHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntityShulker shulker = (EntityShulker) possessivePlayer.getPossessing();
        BlockPos attachmentPos = shulker.getAttachmentPos();
        if (attachmentPos != null) {
            player.posX = attachmentPos.getX();
            player.posY = attachmentPos.getY();
            player.posZ = attachmentPos.getZ();
            player.prevPosX = attachmentPos.getX();
            player.prevPosY = attachmentPos.getY();
            player.prevPosZ = attachmentPos.getZ();
            player.motionX = 0.0F;
            player.motionY = 0.0F;
            player.motionZ = 0.0F;
            player.moveForward = 0.0F;
            player.moveStrafing = 0.0F;
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "shulker");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityShulker.class;
    }
}
