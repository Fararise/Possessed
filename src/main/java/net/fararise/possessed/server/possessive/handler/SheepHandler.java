package net.fararise.possessed.server.possessive.handler;

import net.fararise.possessed.Possessed;
import net.fararise.possessed.server.api.EntityPossessHandler;
import net.fararise.possessed.server.possessive.PossessivePlayer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class SheepHandler implements EntityPossessHandler {
    @Override
    public void onUpdate(PossessivePlayer possessivePlayer, EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NBTTagCompound data = this.getData(player);
            int eatCooldown = data.getShort("EatCooldown");
            if (eatCooldown > 0) {
                eatCooldown--;
                data.setShort("EatCooldown", (short) eatCooldown);
            }
        }
    }

    @Override
    public void onClickBlock(PossessivePlayer possessivePlayer, EntityPlayer player, IBlockState state, BlockPos pos) {
        EntitySheep possessing = (EntitySheep) possessivePlayer.getPossessing();
        if (!player.worldObj.isRemote && this.getData(player).getShort("EatCooldown") <= 0 && possessing.getSheared()) {
            if (state.getBlock() == Blocks.GRASS) {
                possessing.setSheared(false);
                player.worldObj.playEvent(2001, pos, Block.getIdFromBlock(Blocks.GRASS));
                player.worldObj.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
                this.getData(player).setShort("EatCooldown", (short) 2000);
            }
        }
    }

    @Override
    public ResourceLocation getIdentifier() {
        return new ResourceLocation(Possessed.MODID, "sheep");
    }

    @Override
    public Class<? extends EntityLivingBase> getEntityClass() {
        return EntitySheep.class;
    }
}
