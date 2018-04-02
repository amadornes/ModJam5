package mod.crystals.block;

import mod.crystals.init.CrystalsBlocks;
import mod.crystals.tile.TileSeal;
import mod.crystals.tile.TileSealExt;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

public class BlockSealExt extends BlockBase implements ITileEntityProvider {

    public BlockSealExt() {
        super(Material.ROCK);
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileSealExt();
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!checkIntegrity(world, pos)) {
            world.setBlockToAir(pos);
            return;
        }
        if (!checkForSupport(world, pos)) {
            dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        BlockPos sealPos = getSealPos(world, pos);
        IBlockState sealState = world.getBlockState(Objects.requireNonNull(sealPos));
        return sealState.getBlock().getPickBlock(sealState, null, world, sealPos, player);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        BlockPos sealPos = getSealPos(world, pos);
        if (sealPos == null) return super.getBoundingBox(state, world, pos);
        IBlockState sealState = world.getBlockState(sealPos);
        if (sealState.getBlock() != CrystalsBlocks.seal)
            return super.getSelectedBoundingBox(state, world, pos);
        return sealState.getSelectedBoundingBox(world, sealPos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        BlockPos sealPos = getSealPos(world, pos);
        if (sealPos == null) return super.getBoundingBox(state, world, pos);
        IBlockState sealState = world.getBlockState(sealPos);
        if (sealState.getBlock() != CrystalsBlocks.seal)
            return super.getBoundingBox(state, world, pos);
        return sealState.getBoundingBox(world, pos).offset(pos.subtract(sealPos)).intersect(FULL_BLOCK_AABB);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
        return null;
    }

    // --------------------------------------------------------------------

    @Nullable
    public static BlockPos getSealPos(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockSeal) return pos;
        if (state.getBlock() instanceof BlockSealExt) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileSealExt) {
                return pos.add(((TileSealExt) te).getOffset());
            }
        }
        return null;
    }

    public static boolean checkIntegrity(IBlockAccess world, BlockPos pos) {
        BlockPos seal = getSealPos(world, pos);
        if (seal == null) return false;
        TileEntity tileEntity = world.getTileEntity(seal);
        if (!(tileEntity instanceof TileSeal)) return false;
        TileSeal te = (TileSeal) tileEntity;
        for (BlockPos cp : te.getSealBox()) {
            if (!Objects.equals(getSealPos(world, cp), seal)) return false;
        }
        return true;
    }

    public static boolean canPlaceAt(IBlockAccess world, BlockPos pos, EnumFacing face, int radius) {
        for (BlockPos cp : getSealBounds(radius, face, pos)) {
            IBlockState state = world.getBlockState(cp);
            if (!checkForSupport(world, cp, face)) return false;
            if (!state.getBlock().isReplaceable(world, cp)) return false;
        }
        return true;
    }

    public static boolean checkForSupport(IBlockAccess world, BlockPos pos, EnumFacing face) {
        BlockPos support = pos.offset(face.getOpposite());
        return world.getBlockState(support).getBlockFaceShape(world, support, face) == BlockFaceShape.SOLID;
    }

    public static boolean checkForSupport(IBlockAccess world, BlockPos pos) {
        @SuppressWarnings("ConstantConditions")
        EnumFacing f = world.getBlockState(getSealPos(world, pos)).getValue(BlockDirectional.FACING);
        return checkForSupport(world, pos, f);
    }

    public static void placeSealExt(World world, BlockPos pos) {
        Iterable<BlockPos> blocks = ((TileSeal) world.getTileEntity(pos)).getSealBox();
        for (BlockPos p : blocks) {
            if (Objects.equals(pos, p)) continue;
            world.setBlockState(p, CrystalsBlocks.seal_ext.getDefaultState());
            ((TileSealExt) world.getTileEntity(p)).setOffset(pos.subtract(p));
        }
    }

    public static Iterable<BlockPos> getSealBounds(int radius, EnumFacing face, BlockPos pos) {
        Vec3d front = new Vec3d(face.getDirectionVec());
        Vec3d off1 = new Vec3d(front.y, front.z, front.x);
        Vec3d off2 = new Vec3d(front.z, front.x, front.y);

        BlockPos p1 = new BlockPos(new Vec3d(pos)
            .add(off1.scale(radius))
            .add(off2.scale(radius)));
        BlockPos p2 = new BlockPos(new Vec3d(pos)
            .add(off1.scale(-radius))
            .add(off2.scale(-radius)));

        return BlockPos.getAllInBox(p1, p2);
    }

}
