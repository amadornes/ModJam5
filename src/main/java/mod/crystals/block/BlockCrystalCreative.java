package mod.crystals.block;

import mod.crystals.tile.TileCrystalCreative;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockCrystalCreative extends BlockCrystalBase {

    public BlockCrystalCreative() {
        setBlockUnbreakable();
    }

    private static final AxisAlignedBB AABB = new AxisAlignedBB(6 / 16F, 0, 6 / 16F, 10 / 16F, 1, 10 / 16F);

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCrystalCreative();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(COLOR)
                .build();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.setTileEntity(pos, new TileCrystalCreative(true)); // TODO: Probably change this
    }

    @Override
    public RayTraceResult laserRayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
        return null;
    }

}
