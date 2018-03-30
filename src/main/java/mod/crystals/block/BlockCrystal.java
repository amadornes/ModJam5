package mod.crystals.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockCrystal extends BlockBase {

    private static final float DIAMETER = 1.5F / 16F;
    private static final float HEIGHT = 8.5F / 16F;
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.5 - DIAMETER, 0, 0.5 - DIAMETER, 0.5 + DIAMETER, HEIGHT, 0.5 + DIAMETER);

    public BlockCrystal() {
        super(Material.GLASS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    protected boolean isFull(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT;
    }
}
