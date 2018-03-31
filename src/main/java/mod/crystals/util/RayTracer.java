package mod.crystals.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class RayTracer {

    public static RayTraceResult rayTrace(World world, Vec3d start, Vec3d end, Predicate<BlockPos> test) {
        if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) return null;
        if (Double.isNaN(end.x) || Double.isNaN(end.y) || Double.isNaN(end.z)) return null;
        int eX = MathHelper.floor(end.x);
        int eY = MathHelper.floor(end.y);
        int eZ = MathHelper.floor(end.z);
        int sX = MathHelper.floor(start.x);
        int sY = MathHelper.floor(start.y);
        int sZ = MathHelper.floor(start.z);

        BlockPos pos = new BlockPos(sX, sY, sZ);
        if (test.test(pos)) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (block.canCollideCheck(state, false)) {
                RayTraceResult hit = state.collisionRayTrace(world, pos, start, end);
                if (hit != null) return hit;
            }
        }

        for (int i = 200; i >= 0; i--) {
            if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) return null;
            if (sX == eX && sY == eY && sZ == eZ) return null;

            boolean flagX = true;
            boolean flagY = true;
            boolean flagZ = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (eX > sX) {
                d0 = (double) sX + 1.0D;
            } else if (eX < sX) {
                d0 = (double) sX + 0.0D;
            } else {
                flagX = false;
            }

            if (eY > sY) {
                d1 = (double) sY + 1.0D;
            } else if (eY < sY) {
                d1 = (double) sY + 0.0D;
            } else {
                flagY = false;
            }

            if (eZ > sZ) {
                d2 = (double) sZ + 1.0D;
            } else if (eZ < sZ) {
                d2 = (double) sZ + 0.0D;
            } else {
                flagZ = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            double d6 = end.x - start.x;
            double d7 = end.y - start.y;
            double d8 = end.z - start.z;

            if (flagX) d3 = (d0 - start.x) / d6;
            if (flagY) d4 = (d1 - start.y) / d7;
            if (flagZ) d5 = (d2 - start.z) / d8;

            if (d3 == -0.0D) d3 = -1.0E-4D;

            if (d4 == -0.0D) d4 = -1.0E-4D;

            if (d5 == -0.0D) d5 = -1.0E-4D;

            EnumFacing side;

            if (d3 < d4 && d3 < d5) {
                side = eX > sX ? EnumFacing.WEST : EnumFacing.EAST;
                start = new Vec3d(d0, start.y + d7 * d3, start.z + d8 * d3);
            } else if (d4 < d5) {
                side = eY > sY ? EnumFacing.DOWN : EnumFacing.UP;
                start = new Vec3d(start.x + d6 * d4, d1, start.z + d8 * d4);
            } else {
                side = eZ > sZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                start = new Vec3d(start.x + d6 * d5, start.y + d7 * d5, d2);
            }

            sX = MathHelper.floor(start.x) - (side == EnumFacing.EAST ? 1 : 0);
            sY = MathHelper.floor(start.y) - (side == EnumFacing.UP ? 1 : 0);
            sZ = MathHelper.floor(start.z) - (side == EnumFacing.SOUTH ? 1 : 0);
            pos = new BlockPos(sX, sY, sZ);
            IBlockState state = world.getBlockState(pos);
            Block block1 = state.getBlock();

            if (block1.canCollideCheck(state, false)) {
                RayTraceResult hit = state.collisionRayTrace(world, pos, start, end);
                if (hit != null) return hit;
            }
        }

        return null;
    }

}
