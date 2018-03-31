package mod.crystals.api.seal;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISeal {

    World getWorld();

    BlockPos getPos();

    EnumFacing getFace();

}
