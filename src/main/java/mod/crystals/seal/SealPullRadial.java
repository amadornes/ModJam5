package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

import static java.lang.Math.*;

public class SealPullRadial extends SealType {

    private static final float RADIUS = 5;
    private static final float MAX_DIST = RADIUS * RADIUS * 3;

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public int getTint() {
        return NatureType.AIR.getColor();
    }

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient wind = new Ingredient(NatureType.AIR);
        Ingredient pull = new Ingredient(NatureType.AIR, NatureType.VOID);
        return new Ingredient[][]{
                {wind, wind, wind},
                {wind, pull, wind},
                {wind, wind, wind}
        };
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private static class Instance extends AbstractSeal {

        public Instance(ISeal seal) {
            super(seal);
        }

        @Override
        public void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption) {
            capacity.accept(NatureType.AIR, 500F);
            consumption.accept(NatureType.AIR, 100F);
        }

        @Override
        public void update() {
            moveEntities(seal, true, this::consumeEnergy);
        }

    }

    public static void moveEntities(ISeal seal, boolean pull, BooleanSupplier consume) {
        if (seal.getWorld().isBlockIndirectlyGettingPowered(seal.getPos()) > 0) return;

        EnumFacing face = seal.getFace().getOpposite();
        AxisAlignedBB bounds = AbstractSeal.getAreaInFront(seal, RADIUS, true);
        Vec3d center = new Vec3d(seal.getPos())
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3d(face.getDirectionVec()).scale(0.5));

        for (Entity entity : seal.getWorld().getEntitiesWithinAABBExcludingEntity(null, bounds)) {
            if (!consume.getAsBoolean()) return;
            Vec3d direction = entity.getPositionVector().subtract(center);
            direction = direction.normalize().scale(0.15 * max(0, MAX_DIST - direction.lengthSquared()) / MAX_DIST).scale(pull ? -1 : 1);
            entity.motionX += direction.x;
            entity.motionY += direction.y;
            entity.motionZ += direction.z;

            if (seal.getWorld().isRemote && direction.lengthSquared() > 0) {
                if (pull) {
                    seal.getWorld().spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY, entity.posZ, direction.x * 5, direction.y * 5, direction.z * 5);
                } else {
                    seal.getWorld().spawnParticle(EnumParticleTypes.CLOUD, center.x, center.y, center.z, direction.x * 5, direction.y * 5, direction.z * 5);
                }
            }
        }
    }

}
