package mod.crystals.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPost extends BlockBase {

    public static final IProperty<PostComponent> COMPONENT = PropertyEnum.create("component", PostComponent.class);
    public static final IProperty<Boolean> NORTH = PropertyBool.create("north");
    public static final IProperty<Boolean> SOUTH = PropertyBool.create("south");
    public static final IProperty<Boolean> WEST = PropertyBool.create("west");
    public static final IProperty<Boolean> EAST = PropertyBool.create("east");

    public BlockPost() {
        super(Material.WOOD);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this)
                .add(COMPONENT, NORTH, SOUTH, WEST, EAST)
                .build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COMPONENT, PostComponent.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COMPONENT).ordinal();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (facing.getAxis() != EnumFacing.Axis.Y) {
            return getDefaultState().withProperty(COMPONENT, PostComponent.SIDE);
        }

        IBlockState below = world.getBlockState(pos.down());
        if (below.getBlock() == this) {
            return getDefaultState().withProperty(COMPONENT, PostComponent.TOP);
        }
        return getDefaultState().withProperty(COMPONENT, PostComponent.BOTTOM);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (state.getValue(COMPONENT) == PostComponent.BOTTOM) return;

        IBlockState above = world.getBlockState(pos.up());
        if (above.getBlock() == this && above.getValue(COMPONENT) == PostComponent.TOP) {
            world.setBlockState(pos, getDefaultState().withProperty(COMPONENT, PostComponent.MIDDLE));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (state.getValue(COMPONENT) == PostComponent.TOP) {
            return state
                    .withProperty(NORTH, isSide(world, pos.north()))
                    .withProperty(SOUTH, isSide(world, pos.south()))
                    .withProperty(WEST, isSide(world, pos.west()))
                    .withProperty(EAST, isSide(world, pos.east()));
        }
        return state
                .withProperty(NORTH, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(EAST, false);
    }

    private boolean isSide(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && state.getValue(COMPONENT) == PostComponent.SIDE;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    public enum PostComponent implements IStringSerializable {
        BOTTOM,
        MIDDLE,
        TOP,
        SIDE;

        public static final PostComponent[] VALUES = values();

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
