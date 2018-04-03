package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;

public class SealPullLinear extends SealType {

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
                {null, wind, null},
                {wind, pull, wind},
                {null, wind, null}
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
        }

        @Override
        public void update() {
            moveEntities(seal, true, 1.5F, this::consumeEnergy);
        }

    }

    public static void moveEntities(ISeal seal, boolean pull, float radius, BooleanSupplier consume) {
        EnumFacing face = seal.getFace();
        Vec3d dv = new Vec3d(face.getDirectionVec());
        Vec3d center = new Vec3d(seal.getPos())
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3d(face.getOpposite().getDirectionVec()).scale(0.5));

        Collection<Entity> entities = seal.getWorld().getEntitiesWithinAABBExcludingEntity(null,
                new AxisAlignedBB(seal.getPos())
                        .expand(dv.x * 10, dv.y * 10, dv.z * 10)
                        .expand(-(1 - dv.x) * radius, -(1 - dv.y) * radius, -(1 - dv.z) * radius)
                        .expand((1 - dv.x) * radius, (1 - dv.y) * radius, (1 - dv.z) * radius)
        );
        for (Entity entity : entities) {
            Vec3d ePos = entity.getPositionVector();

            Vec3d dif = ePos.subtract(center);
            dif = dif.subtract(dif.x * Math.abs(dv.x), dif.y * Math.abs(dv.y), dif.z * Math.abs(dv.z));
            if (dif.lengthSquared() > radius * radius)continue;

            if (!consume.getAsBoolean()) break;
            dif = ePos.subtract(center).scale(pull ? -1 : 1);
            Vec3d dir = pull ? (dif.lengthSquared() > 1 ? dif.normalize() : dif).scale(0.1) : dv.normalize().scale(0.1);
            entity.motionX += dir.x;
            entity.motionY += dir.y + 0.02;
            entity.motionZ += dir.z;
        }
    }

}
