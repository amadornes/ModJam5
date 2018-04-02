package mod.crystals.crystal;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface ILaserSource {

    Vec3d getPosition(float partialTicks);

    Vec3d getColor(float partialTicks);

    default boolean shouldIgnore(BlockPos pos) {
        return pos.equals(new BlockPos(getPosition(0)));
    }

    default void onConnect(ILaserSource other, Ray ray) {
    }

    default void onDisconnect(ILaserSource other, Ray ray) {
    }

}
