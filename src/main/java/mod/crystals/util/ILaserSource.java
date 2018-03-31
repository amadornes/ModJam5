package mod.crystals.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface ILaserSource {

    Vec3d getPosition(float partialTicks);

    Vec3d getColor(float partialTicks);

    default boolean shouldIgnore(BlockPos pos) {
        return pos.equals(new BlockPos(getPosition(0)));
    }

}
