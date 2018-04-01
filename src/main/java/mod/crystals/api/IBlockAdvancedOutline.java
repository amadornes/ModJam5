package mod.crystals.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public interface IBlockAdvancedOutline {
    /**
     * Replaces getSelectedBoundingBox.
     */
    public Collection<AxisAlignedBB> getOutlineBoxes(World world, BlockPos pos, IBlockState state);
}
