package mod.crystals.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface ILaserRayTrace {

    RayTraceResult laserRayTrace(World world, BlockPos pos, Vec3d start, Vec3d end);

}
