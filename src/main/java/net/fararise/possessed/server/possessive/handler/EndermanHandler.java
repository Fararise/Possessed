package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;

public class EndermanHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntityEnderman enderman = (EntityEnderman) possessivePlayer.getPossessing();
        IBlockState state = null;
        ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);
        if (heldItem != null && heldItem.getItem() instanceof ItemBlock) {
            state = ((ItemBlock) heldItem.getItem()).getBlock().getStateFromMeta(heldItem.getItemDamage() & 15);
        }
        enderman.setHeldBlockState(state);
    }

    @Override
    public void onClickAir(PossessivePlayer possessivePlayer, EntityPlayer player) {
        RayTraceResult result = ForgeHooks.rayTraceEyes(player, 64);
        if (result != null && result.typeOfHit != RayTraceResult.Type.MISS) {
            if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = result.getBlockPos().offset(result.sideHit);
                player.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            } else {
                Entity entityHit = result.entityHit;
                player.setPosition(entityHit.posX, entityHit.posY, entityHit.posZ);
            }
            if (!player.capabilities.isCreativeMode) {
                player.attackEntityFrom(DamageSource.fall, 4.0F);
            }
            player.worldObj.playSound(null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
            player.worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
        }
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntityEnderman.class;
    }
}
