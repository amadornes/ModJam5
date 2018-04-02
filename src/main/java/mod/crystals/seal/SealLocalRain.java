package mod.crystals.seal;

import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import mod.crystals.client.particle.ParticleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static mod.crystals.client.particle.ParticleType.posVelocity;

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

//    @Override
//    public ResourceLocation getGlowyTextureLocation(TextureType type) {
//        switch (type) {
//            case GLOWY_BLACK:
//                return new ResourceLocation(CrystalsMod.MODID, "textures/seals/base/glowy_thing_black.png");
//            case GLOWY_TRANSPARENT:
//                return new ResourceLocation(CrystalsMod.MODID, "textures/seals/base/glowy_thing.png");
//            case GLOWY_SHIMMER:
//                //return new ResourceLocation(CrystalsMod.MODID, "textures/seals/pushpull/glowy_thing_shimmer.png");
//        }
//        return null;
//    }

    @Override
    public int getGlowyColor() {
        return NatureType.WATER.getColor();
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private static class Instance extends AbstractSeal {

        private final ISeal seal;

        public Instance(ISeal seal) {
            super(seal);
            this.seal = seal;
        }

        @Override
        public void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption) {
            capacity.accept(NatureType.WATER, 100f);
            consumption.accept(NatureType.WATER, 10f);
        }

        @SuppressWarnings("SuspiciousNameCombination")
        @Override
        public void update() {
            if (!consumeEnergy()) return;

            World world = seal.getWorld();
            EnumFacing face = seal.getFace();

            Vec3d center = new Vec3d(seal.getPos())
                    .addVector(0.5, 0.5, 0.5)
                    .add(new Vec3d(face.getOpposite().getDirectionVec()).scale(0.5));

            Vec3d front = new Vec3d(face.getDirectionVec());
            Vec3d perp1 = new Vec3d(front.y, front.z, front.x);
            Vec3d perp2 = new Vec3d(front.z, front.x, front.y);

            spawnParticles(world, face, center, front, perp1, perp2);
            simulateRain(world, seal.getPos(), face, center, front, perp1, perp2);
        }

        private void spawnParticles(World world, EnumFacing face, Vec3d center, Vec3d front, Vec3d perp1, Vec3d perp2) {
            for (int i = -13; i <= 13; i++) {
                for (int j = -13; j <= 13; j++) {
                    if (i * i + j * j > 13 * 13) continue;
                    if (Math.random() > 1 / 64F) continue;
                    double speed = (0.75 + 0.25 * Math.random()) * 0.125;
                    double off = Math.random() * 0.125;
                    Vec3d pos = center.add(perp1.scale(i / 10F)).add(perp2.scale(j / 10F)).add(front.scale(off));
                    if (face.getAxis() != EnumFacing.Axis.Y) {
                        int h = face.getAxis() == EnumFacing.Axis.X ? j : i;
                        speed += (0.75 + (26 - h) / 26F) * 0.25;
                    }
                    CrystalsMod.proxy.spawnParticle(world, ParticleType.RAIN, posVelocity(
                            pos.x, pos.y, pos.z,
                            front.x * speed, front.y * speed, front.z * speed));
                }
            }
        }

        private void simulateRain(World world, BlockPos sealPos, EnumFacing face, Vec3d center, Vec3d front, Vec3d perp1, Vec3d perp2) {
            Set<BlockPos> visited = new HashSet<>();
            visited.add(sealPos);
            for (int i = -3; i <= 3; i++) {
                for (int j = -3; j <= 3; j++) {
                    if (i * i + j * j > 3 * 3) continue;
                    Vec3d start = center.add(perp1.scale(i / 3F)).add(perp2.scale(j / 3F));
                    Vec3d current = start;
                    double speed = 0.875 * 0.125;
                    if (face.getAxis() != EnumFacing.Axis.Y) {
                        int h = face.getAxis() == EnumFacing.Axis.X ? j : i;
                        speed += (1 + (6.5 - h) / 6F) * 0.25;
                    }
                    Vec3d vel = front.scale(speed);
                    for (int k = 0; k < 50; k++) {
                        BlockPos pos = new BlockPos(current);
                        if (visited.add(pos)) {
                            if (tryWater(world, pos, true) || isSolid(world, pos, current)) {
                                break;
                            }
                        }

                        double vx = Math.copySign(Math.max(0, Math.abs(vel.x) - 0.05), vel.x);
                        double vy = (vel.y - 0.06) * 0.98;
                        double vz = Math.copySign(Math.max(0, Math.abs(vel.z) - 0.05), vel.z);
                        vel = new Vec3d(vx, vy, vz);

                        current = current.add(vel);
                    }
                }
            }
        }

        private boolean isSolid(World world, BlockPos pos, Vec3d current) {
            List<AxisAlignedBB> list = new ArrayList<>();
            world.getBlockState(pos).addCollisionBoxToList(world, pos, Block.FULL_BLOCK_AABB.offset(pos), list, null, false);
            for (AxisAlignedBB aabb : list) {
                if (aabb.contains(current)) {
                    return true;
                }
            }
            return false;
        }

        private boolean tryWater(World world, BlockPos pos, boolean firstPass) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockFarmland) {
                world.setBlockState(pos, state.withProperty(BlockFarmland.MOISTURE, Math.min(state.getValue(BlockFarmland.MOISTURE) + 2, 7)));
                return true;
            }
            return firstPass && tryWater(world, pos.down(), false);
        }

    }

}
