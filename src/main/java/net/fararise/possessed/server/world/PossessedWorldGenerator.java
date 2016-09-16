package net.fararise.possessed.server.world;

import com.google.common.base.Predicate;
import net.fararise.possessed.server.block.BlockRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class PossessedWorldGenerator implements IWorldGenerator {
    private static final BlockPos.MutableBlockPos POSITION = new BlockPos.MutableBlockPos();
    private static final WorldGenSingleOre POSSESSITITE_GEN = new WorldGenSingleOre(BlockRegistry.POSSESSITITE_ORE.getDefaultState());

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        if (world.provider.getDimension() == 0) {
            this.generateOre(PossessedWorldGenerator.POSSESSITITE_GEN, world, random, 4, 16, chunkX, chunkZ, 3, 5);
        }
    }

    private void generateOre(WorldGenerator generator, World world, Random random, int minHeight, int maxHeight, int chunkX, int chunkZ, int minCount, int maxCount) {
        int chunkCount = maxCount == minCount ? minCount : random.nextInt(maxCount - minCount) + minCount;
        for (int i = 0; i < chunkCount; i++) {
            int x = (chunkX * 16) + random.nextInt(16);
            int y = random.nextInt(maxHeight - minHeight) + minHeight;
            int z = (chunkZ * 16) + random.nextInt(16);
            PossessedWorldGenerator.POSITION.setPos(x, y, z);
            generator.generate(world, random, PossessedWorldGenerator.POSITION);
        }
    }

    private static class WorldGenSingleOre extends WorldGenerator {
        private static final Predicate<IBlockState> DEFAULT_PREDICATE = BlockMatcher.forBlock(Blocks.STONE);

        private final IBlockState state;
        private final Predicate<IBlockState> predicate;

        public WorldGenSingleOre(IBlockState state) {
            this(state, WorldGenSingleOre.DEFAULT_PREDICATE);
        }

        public WorldGenSingleOre(IBlockState state, Predicate<IBlockState> predicate) {
            this.state = state;
            this.predicate = predicate;
        }

        @Override
        public boolean generate(World world, Random rand, BlockPos position) {
            IBlockState state = world.getBlockState(position);
            if (state.getBlock().isReplaceableOreGen(state, world, position, this.predicate)) {
                world.setBlockState(position, this.state);
                return true;
            }
            return false;
        }
    }
}
