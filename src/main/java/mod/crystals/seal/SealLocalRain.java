package mod.crystals.seal;

import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import mod.crystals.client.particle.ParticleType;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static mod.crystals.client.particle.ParticleType.*;

public class SealLocalRain extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient rain = new Ingredient(NatureType.WATER, NatureType.AIR);
        return new Ingredient[][]{
            {rain, rain, rain},
            {rain, rain, rain},
            {rain, rain, rain}
        };
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private static class Instance implements ISealInstance {

        private final ISeal seal;

        public Instance(ISeal seal) {
            this.seal = seal;
        }

        @SuppressWarnings("SuspiciousNameCombination")
        @Override
        public void update() {
            BlockPos pos = seal.getPos();
            World world = seal.getWorld();
            Vec3d front = new Vec3d(seal.getFace().getDirectionVec());
            Vec3d skew1 = new Vec3d(front.z, front.x, front.y);
            Vec3d skew2 = new Vec3d(front.y, front.z, front.x);

            for (int i = 0; i < 10; i++) {
                // sorry for the scala ;_;
                // this time there's no Math.whatever to save you >:D
                Vec3d velocity =
                    front.scale(1.0 + world.rand.nextGaussian() * 0.25).scale(0.25)
                        .add(skew1.scale(world.rand.nextGaussian() * 0.1))
                        .add(skew2.scale(world.rand.nextGaussian() * 0.1));
                Vec3d start = new Vec3d(pos).addVector(0.5, 0.5, 0.5);

                CrystalsMod.proxy.spawnParticle(world, ParticleType.RAIN, posVelocity(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, velocity.x, velocity.y, velocity.z));

                // particle simulator
                if (!world.isRemote && Math.random() > 0.75) {
                    Vec3d pt = start;
                    Vec3d vel = velocity;
                    int tickCount = 1000; // simulate max 1000 ticks
                    while (tickCount-- > 0) {
                        vel = vel.addVector(0, -0.06, 0); // gravity
                        Vec3d newPos = pt.add(vel);
                        vel = vel.scale(0.9800000190734863); // air resistance
                        if (newPos.y < 0) break;
                        AxisAlignedBB box = new AxisAlignedBB(pt.x, pt.y, pt.z, newPos.x, newPos.y, newPos.z);
                        List<AxisAlignedBB> boxes = world.getCollisionBoxes(null, box);
                        for (AxisAlignedBB aabb : boxes) {
                            if (!aabb.intersects(Math.min(pt.x, newPos.x), Math.min(pt.y, newPos.y), Math.min(pt.z, newPos.z), Math.max(pt.x, newPos.x), Math.max(pt.y, newPos.y), Math.max(pt.z, newPos.z))) continue;
                            RayTraceResult result = aabb.calculateIntercept(pt, newPos);
                            if (result == null) continue;
                            BlockPos center = new BlockPos(result.hitVec);
                            for (BlockPos p : BlockPos.getAllInBox(center.offset(result.sideHit), center.offset(result.sideHit.getOpposite()))) {
                                IBlockState state = world.getBlockState(p);
                                if (state.getBlock() instanceof BlockFarmland) {
                                    world.setBlockState(p, state.withProperty(BlockFarmland.MOISTURE, Math.min(state.getValue(BlockFarmland.MOISTURE) + 2, 7)));
                                }
                            }
                        }
                        pt = newPos;
                    }
                }
            }
        }

    }

}
