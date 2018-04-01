package mod.crystals.block;

import mod.crystals.api.ILaserRayTrace;
import mod.crystals.api.IResonant;
import mod.crystals.tile.TileCrystal;
import mod.crystals.util.UnlistedPropertyInt;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockCrystal extends BlockBase implements ITileEntityProvider, ILaserRayTrace {

    private static final AxisAlignedBB[] AABBS_GROUND = {
            new AxisAlignedBB(6 / 16F, 0, 6 / 16F, 10 / 16F, 8.5 / 16F, 10 / 16F),
            new AxisAlignedBB(6 / 16F, 7.5 / 16F, 6 / 16F, 10 / 16F, 1, 10 / 16F),
            new AxisAlignedBB(6 / 16F, 6 / 16F, 0, 10 / 16F, 10 / 16F, 8.5 / 16F),
            new AxisAlignedBB(6 / 16F, 6 / 16F, 7.5 / 16F, 10 / 16F, 10 / 16F, 1),
            new AxisAlignedBB(0, 6 / 16F, 6 / 16F, 8.5 / 16F, 10 / 16F, 10 / 16F),
            new AxisAlignedBB(7.5 / 16F, 6 / 16F, 6 / 16F, 1, 10 / 16F, 10 / 16F)
    };
    private static final AxisAlignedBB AABB_FLOATING =
            new AxisAlignedBB(6.5 / 16F, 3.75 / 16F, 6.5 / 16F, 9.5 / 16F, 12.25 / 16F, 9.5 / 16F);

    private static final AxisAlignedBB[] RT_AABBS_GROUND = {
            new AxisAlignedBB(7 / 16F, 2 / 16F, 7 / 16F, 9 / 16F, 6 / 16F, 9 / 16F),
            new AxisAlignedBB(7 / 16F, 10 / 16F, 7 / 16F, 9 / 16F, 14 / 16F, 9 / 16F),
            new AxisAlignedBB(7 / 16F, 7 / 16F, 2 / 16F, 9 / 16F, 9 / 16F, 6 / 16F),
            new AxisAlignedBB(7 / 16F, 7 / 16F, 10 / 16F, 9 / 16F, 9 / 16F, 14 / 16F),
            new AxisAlignedBB(2 / 16F, 7 / 16F, 7 / 16F, 6 / 16F, 9 / 16F, 9 / 16F),
            new AxisAlignedBB(10 / 16F, 7 / 16F, 7 / 16F, 14 / 16F, 9 / 16F, 9 / 16F)
    };
    private static final AxisAlignedBB RT_AABB_FLOATING =
            new AxisAlignedBB(7 / 16F, 6 / 16F, 7 / 16F, 9 / 16F, 10 / 16F, 9 / 16F);
    private static final AxisAlignedBB[] RT_AABBS_BASE = {
            new AxisAlignedBB(6 / 16F, 0, 6 / 16F, 10 / 16F, 4 / 16F, 10 / 16F),
            new AxisAlignedBB(6 / 16F, 12 / 16F, 6 / 16F, 10 / 16F, 1, 10 / 16F),
            new AxisAlignedBB(0, 6 / 16F, 6 / 16F, 4 / 16F, 10 / 16F, 10 / 16F),
            new AxisAlignedBB(12 / 16F, 6 / 16F, 6 / 16F, 1, 10 / 16F, 10 / 16F),
            new AxisAlignedBB(6 / 16F, 6 / 16F, 0, 10 / 16F, 10 / 16F, 4 / 16F),
            new AxisAlignedBB(6 / 16F, 6 / 16F, 12 / 16F, 10 / 16F, 10 / 16F, 1)
    };

    public static final IProperty<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
    public static final IUnlistedProperty<Integer> COLOR = new UnlistedPropertyInt("color");

    public BlockCrystal() {
        super(Material.GLASS);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCrystal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(VARIANT)
                .add(COLOR)
                .build();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, Variant.VALUES[meta]);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te == null) return state;
        IResonant.Default resonant = (IResonant.Default) te.getCapability(IResonant.CAPABILITY, null);
        return ((IExtendedBlockState) state).withProperty(COLOR, resonant.getColor());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        Variant variant = state.getValue(VARIANT);
        if (variant == Variant.FLOATING) return AABB_FLOATING;
        return AABBS_GROUND[variant.ordinal() - 1];
    }

    @Override
    public RayTraceResult laserRayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
        Variant variant = world.getBlockState(pos).getValue(VARIANT);

        AxisAlignedBB crystalAABB;
        if (variant == Variant.FLOATING) {
            crystalAABB = RT_AABB_FLOATING;
        } else {
            crystalAABB = RT_AABBS_GROUND[variant.ordinal() - 1];
        }

        RayTraceResult hit = crystalAABB.offset(pos).calculateIntercept(start, end);

        if (variant != Variant.FLOATING && hit == null) {
            hit = RT_AABBS_BASE[variant.ordinal() - 1].offset(pos).calculateIntercept(start, end);
        }

        return hit;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        world.setTileEntity(pos, new TileCrystal(true)); // TODO: Probably change this
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileCrystal crystal = (TileCrystal) world.getTileEntity(pos);
        crystal.doJoin(); // TODO: Probably change this
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return super.canPlaceBlockOnSide(world, pos, side) &&
                canStayAt(getStateForPlacement(world, pos, side, 0, 0, 0, 0, null, null), world, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (state.getValue(VARIANT) == Variant.FLOATING) return;

        if (!canStayAt(state, world, pos)) {
            state.getBlock().dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    public boolean canStayAt(IBlockState state, IBlockAccess world, BlockPos pos) {
        Variant variant = state.getValue(VARIANT);
        if (variant == Variant.FLOATING) return true;
        EnumFacing side = EnumFacing.getFront(variant.ordinal() - 1);
        BlockFaceShape shape = world.getBlockState(pos.offset(side)).getBlockFaceShape(world, pos, side.getOpposite());
        return shape != BlockFaceShape.UNDEFINED && shape != BlockFaceShape.BOWL;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand) {
        if (placer == null || placer.isSneaking()) {
            return getDefaultState().withProperty(VARIANT, Variant.VALUES[facing.getOpposite().ordinal() + 1]);
        } else {
            return getDefaultState().withProperty(VARIANT, Variant.FLOATING);
        }
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return state.getValue(VARIANT) != Variant.FLOATING && super.canRenderInLayer(state, layer);
    }

    public enum Variant implements IStringSerializable {
        FLOATING,
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST;

        public static final Variant[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
