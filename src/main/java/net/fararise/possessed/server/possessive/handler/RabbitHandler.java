package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RabbitHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntityRabbit rabbit = (EntityRabbit) possessivePlayer.getPossessing();
        double speedX = player.posX - player.prevPosX;
        double speedZ = player.posZ - player.prevPosZ;
        double speed = speedX * speedX + speedZ * speedZ;
        if (player.onGround && (speed > 0.01 || speed < -0.01)) {
            player.jump();
            rabbit.startJumping();
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.posY - 0.20000000298023224D);
            int z = MathHelper.floor_double(player.posZ);
            IBlockState state = player.worldObj.getBlockState(new BlockPos(x, y, z));
            if (state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                for (int i = 0; i < 3; i++) {
                    player.worldObj.spawnParticle(EnumParticleTypes.BLOCK_CRACK, player.posX + (player.getRNG().nextFloat() - 0.5D) * player.width, player.getEntityBoundingBox().minY + 0.1D, player.posZ + (player.getRNG().nextFloat() - 0.5D) * player.width, -player.motionX * 4.0D, 1.5D, -player.motionZ * 4.0D, Block.getStateId(state));
                }
            }
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "rabbit");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityRabbit.class;
    }

    @Override
    public boolean isEventHandler() {
        return true;
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        IBlockState block = event.getEntityPlayer().worldObj.getBlockState(event.getPos());
        if (block.getMaterial() == Material.GROUND || block.getMaterial() == Material.GRASS) {
            event.setNewSpeed(event.getOriginalSpeed() * 2.0F);
        }
    }
}
