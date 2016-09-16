package net.fararise.possessed.server.block;

import net.fararise.possessed.server.item.ItemRegistry;
import net.fararise.possessed.server.tab.TabRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

import java.util.Random;

public class BlockPossessititeOre extends Block {
    public BlockPossessititeOre() {
        super(Material.ROCK, MapColor.PURPLE);
        this.setHardness(1.5F);
        this.setHarvestLevel("pickaxe", 2);
        this.setUnlocalizedName("possessitite_ore");
        this.setCreativeTab(TabRegistry.POSSESSED_TAB);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemRegistry.POSSESSITITE;
    }
}
