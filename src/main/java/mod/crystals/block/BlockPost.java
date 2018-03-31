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
import org.apache.commons.lang3.ArrayUtils;

import java.util.EnumSet;
import java.util.stream.Stream;

import static mod.crystals.block.BlockPost.PostComponent.*;
import static net.minecraft.block.state.BlockFaceShape.*;

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
        return getDefaultState().withProperty(COMPONENT, VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COMPONENT).ordinal();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (facing.getAxis() != EnumFacing.Axis.Y) {
            return getDefaultState().withProperty(COMPONENT, SIDE);
        }

        IBlockState below = world.getBlockState(pos.down());
        if (below.getBlock() == this) {
            return getDefaultState().withProperty(COMPONENT, TOP);
        }
        return getDefaultState().withProperty(COMPONENT, BOTTOM);
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return super.canPlaceBlockOnSide(worldIn, pos, side) &&
            canStayAt(getStateForPlacement(worldIn, pos, side, 0, 0, 0, 0, null, null), worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (state.getValue(COMPONENT) == BOTTOM) return;

        if (!canStayAt(state, world, pos)) {
            state.getBlock().dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            return;
        }

        updateState(state, world, pos);
    }

    public void updateState(IBlockState state, World world, BlockPos pos) {
        IBlockState above = world.getBlockState(pos.up());

        switch (state.getValue(COMPONENT)) {
            case BOTTOM:
                break;
            case MIDDLE:
                if (above.getBlock() != this)
                    world.setBlockState(pos, state.withProperty(COMPONENT, TOP));
                break;
            case TOP:
                if (above.getBlock() == this && EnumSet.of(TOP, MIDDLE).contains(above.getValue(COMPONENT)))
                    world.setBlockState(pos, state.withProperty(COMPONENT, MIDDLE));
                break;
            case SIDE:
                break;
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(COMPONENT)) {
            case BOTTOM:
                return state
                    .withProperty(NORTH, false)
                    .withProperty(SOUTH, false)
                    .withProperty(WEST, false)
                    .withProperty(EAST, false);
            case MIDDLE:
            case TOP:
                return state
                    .withProperty(NORTH, isPostType(world, pos.north(), SIDE))
                    .withProperty(SOUTH, isPostType(world, pos.south(), SIDE))
                    .withProperty(WEST, isPostType(world, pos.west(), SIDE))
                    .withProperty(EAST, isPostType(world, pos.east(), SIDE));
            case SIDE:
                return state
                    .withProperty(NORTH, isPostType(world, pos.north(), MIDDLE, TOP))
                    .withProperty(SOUTH, isPostType(world, pos.south(), MIDDLE, TOP))
                    .withProperty(WEST, isPostType(world, pos.west(), MIDDLE, TOP))
                    .withProperty(EAST, isPostType(world, pos.east(), MIDDLE, TOP));
        }
        throw new IllegalStateException("something went very wrong!");
    }

    public boolean canStayAt(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(COMPONENT)) {
            case BOTTOM:
                return EnumSet.of(CENTER, CENTER_BIG, CENTER_SMALL, MIDDLE_POLE, MIDDLE_POLE_THICK, MIDDLE_POLE_THIN, SOLID)
                    .contains(world.getBlockState(pos.down()).getBlockFaceShape(world, pos.down(), EnumFacing.UP));
            case MIDDLE:
            case TOP:
                return isPostType(world, pos.down(), TOP, MIDDLE, BOTTOM);
            case SIDE:
                return Stream.of(
                    isPostType(world, pos.north(), TOP, MIDDLE),
                    isPostType(world, pos.south(), TOP, MIDDLE),
                    isPostType(world, pos.west(), TOP, MIDDLE),
                    isPostType(world, pos.east(), TOP, MIDDLE)
                )
                    .filter(it -> it)
                    .count() == 1;
        }
        throw new IllegalStateException("something went very wrong!");
    }

    private boolean isPostType(IBlockAccess world, BlockPos pos, PostComponent... validTypes) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && ArrayUtils.contains(validTypes, state.getValue(COMPONENT));
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
