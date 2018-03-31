package mod.crystals.block;

import mod.crystals.creativetab.CreativeTabCrystals;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

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

}
