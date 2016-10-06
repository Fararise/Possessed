package net.fararise.possessed.server.block;

import net.fararise.possessed.Possessed;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockRegistry {
    public static final BlockPossessititeOre POSSESSITITE_ORE = new BlockPossessititeOre();

    public static void onPreInit() {
        BlockRegistry.register(BlockRegistry.POSSESSITITE_ORE, new ResourceLocation(Possessed.MODID, "possessitite_ore"));
    }

    private static void register(Block block, ResourceLocation identifier) {
        GameRegistry.register(block, identifier);
        GameRegistry.register(new ItemBlock(block), identifier);
    }

    private static void register(Block block, ResourceLocation identifier, Class<? extends TileEntity> tile) {
        BlockRegistry.register(block, identifier);
        GameRegistry.registerTileEntity(tile, identifier.toString());
    }
}
