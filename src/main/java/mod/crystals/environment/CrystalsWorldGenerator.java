package mod.crystals.environment;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.block.BlockCrystal;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.tile.TileCrystal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class CrystalsWorldGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        GeneratorCrystals gen = new GeneratorCrystals();
        for (int i = 0; i < 2; i++) {
            for (int i1 = 0; i1 < 10; i1++) {
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = random.nextInt(128) + 12;
                BlockPos blockPos = new BlockPos(x, y, z);
                if (gen.generate(world, random, blockPos)) break;
            }
        }
    }

    // major hax, for now, time is running out D:
    public static class GeneratorCrystals extends WorldGenMinable {

        public GeneratorCrystals() {
            super(CrystalsBlocks.crystal.getDefaultState(), 1);
        }

        public boolean generate(World worldIn, Random rand, BlockPos position) {
            float f = rand.nextFloat() * (float) Math.PI;
            double d0 = (double) ((float) (position.getX() + 8) + MathHelper.sin(f) * 1 / 8.0F);
            double d2 = (double) ((float) (position.getZ() + 8) + MathHelper.cos(f) * 1 / 8.0F);
            double d4 = (double) (position.getY() + rand.nextInt(3) - 2);

            double d9 = rand.nextDouble() / 16.0D;
            double d10 = (double) 1.0F * d9 + 1.0D;
            double d11 = (double) 1.0F * d9 + 1.0D;
            int j = MathHelper.floor(d0 - d10 / 2.0D);
            int k = MathHelper.floor(d4 - d11 / 2.0D);
            int l = MathHelper.floor(d2 - d10 / 2.0D);

            BlockPos blockpos = new BlockPos(j, k, l);

            IBlockState state = worldIn.getBlockState(blockpos);

            EnumFacing attachSide = null;
            for (EnumFacing facing : EnumFacing.VALUES) {
                BlockPos base = blockpos.offset(facing);
                if (state.getBlock().isReplaceable(worldIn, blockpos) &&
                    worldIn.getBlockState(base).isSideSolid(worldIn, base, facing.getOpposite())) {
                    attachSide = facing;
                    break;
                }
            }
            if (attachSide == null) return false;

            if (!worldIn.canBlockSeeSky(blockpos)) {
                worldIn.setBlockState(blockpos, CrystalsBlocks.crystal.getDefaultState().withProperty(BlockCrystal.VARIANT, BlockCrystal.Variant.valueOf(attachSide.name())), 2);
                TileCrystal te = (TileCrystal) worldIn.getTileEntity(blockpos);
                BiomeEnvironmentScanner bes = new BiomeEnvironmentScanner();
                IResonant resonant = te.getCapability(IResonant.CAPABILITY, null);
                TObjectFloatMap<NatureType> map = new TObjectFloatHashMap<>();
                bes.compute(worldIn, blockpos, map::put);
                resonant.setNatureAmounts(map);
                resonant.setResonance(1.0f);
                return true;
            } else {
                return false;
            }

        }
    }
}
