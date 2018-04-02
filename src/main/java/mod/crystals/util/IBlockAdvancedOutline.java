package mod.crystals.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public interface IBlockAdvancedOutline {

    /**
     * Replaces getSelectedBoundingBox.
     */
    Collection<AxisAlignedBB> getOutlineBoxes(World world, BlockPos pos, IBlockState state);

}
