package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SnowmanHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        EntitySnowman possessing = (EntitySnowman) possessivePlayer.getPossessing();
        if (player.onGround) {
            EnchantmentFrostWalker.freezeNearby(player, player.worldObj, player.getPosition(), 2);
            for (int i = 0; i < 4; ++i) {
                int x = MathHelper.floor_double(player.posX + (i % 2 * 2 - 1) * 0.25F);
                int y = MathHelper.floor_double(player.posY);
                int z = MathHelper.floor_double(player.posZ + (i / 2 % 2 * 2 - 1) * 0.25F);
                BlockPos pos = new BlockPos(x, y, z);
                IBlockState state = player.worldObj.getBlockState(pos);
                if (state.getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canPlaceBlockAt(player.worldObj, pos)) {
                    player.worldObj.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
                }
            }
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "snowman");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySnowman.class;
    }
}
