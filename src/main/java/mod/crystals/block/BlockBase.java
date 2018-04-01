package mod.crystals.block;

import mod.crystals.creativetab.CreativeTabCrystals;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockBase extends Block {

    public BlockBase(Material material) {
        super(material);
        setCreativeTab(CreativeTabCrystals.instance);
    }

    protected boolean isFull(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return isFull(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return isFull(state);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        if (!isFull(state)) return BlockFaceShape.UNDEFINED;
        return super.getBlockFaceShape(world, state, pos, face);
    }
}
