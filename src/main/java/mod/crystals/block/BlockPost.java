package mod.crystals.block;

import mod.crystals.api.IBlockAdvancedOutline;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import static mod.crystals.block.BlockPost.PostComponent.*;
import static net.minecraft.block.state.BlockFaceShape.*;

public class BlockPost extends BlockBase implements IBlockAdvancedOutline {

    public static final IProperty<PostComponent> COMPONENT = PropertyEnum.create("component", PostComponent.class);
    public static final IProperty<Boolean> NORTH = PropertyBool.create("north");
    public static final IProperty<Boolean> SOUTH = PropertyBool.create("south");
    public static final IProperty<Boolean> WEST = PropertyBool.create("west");
    public static final IProperty<Boolean> EAST = PropertyBool.create("east");

    public static final AxisAlignedBB MIDDLE_AABB = new AxisAlignedBB(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
    public static final AxisAlignedBB BOTTOM_AABB = new AxisAlignedBB(0.3125, 0.0, 0.3125, 0.6875, 0.75, 0.6875);
    public static final AxisAlignedBB SIDE_AABB = new AxisAlignedBB(0.375, 0.625, 0.375, 0.625, 1.0, 0.625);
    public static final List<AxisAlignedBB> EXT_AABB = Collections.unmodifiableList(Arrays.asList(
        new AxisAlignedBB(0.4375, 0.75, 0.625, 0.5625, 0.9375, 1.0),
        new AxisAlignedBB(0.0, 0.75, 0.4375, 0.375, 0.9375, 0.5625),
        new AxisAlignedBB(0.4375, 0.75, 0.0, 0.5625, 0.9375, 0.375),
        new AxisAlignedBB(0.625, 0.75, 0.4375, 1.0, 0.9375, 0.5625)
    ));

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

    public Collection<AxisAlignedBB> getBoundingBoxes(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = state.getActualState(world, pos);
        Collection<AxisAlignedBB> boxes = new ArrayList<>();
        switch (state.getValue(COMPONENT)) {
            case BOTTOM:
                boxes.add(BOTTOM_AABB);
            case SIDE:
                boxes.add(SIDE_AABB);
                break;
            case MIDDLE:
            case TOP:
            boxes.add(MIDDLE_AABB);
            break;
        }

        if (state.getValue(NORTH)) boxes.add(EXT_AABB.get(EnumFacing.NORTH.getHorizontalIndex()));
        if (state.getValue(SOUTH)) boxes.add(EXT_AABB.get(EnumFacing.SOUTH.getHorizontalIndex()));
        if (state.getValue(WEST)) boxes.add(EXT_AABB.get(EnumFacing.WEST.getHorizontalIndex()));
        if (state.getValue(EAST)) boxes.add(EXT_AABB.get(EnumFacing.EAST.getHorizontalIndex()));

        return boxes;
    }

    @Override
    public Collection<AxisAlignedBB> getOutlineBoxes(World world, BlockPos pos, IBlockState state) {
        return getBoundingBoxes(state, world, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return getBoundingBoxes(state, source, pos).stream().reduce(null, (acc, a) -> acc == null ? a : acc.union(a));
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        getBoundingBoxes(state, world, pos).stream()
                .map(it -> it.offset(pos))
                .filter(entityBox::intersects)
                .forEach(collidingBoxes::add);
    }

    private boolean isPostType(IBlockAccess world, BlockPos pos, PostComponent... validTypes) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() == this && ArrayUtils.contains(validTypes, state.getValue(COMPONENT));
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.UP ? CENTER : UNDEFINED;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT;
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
