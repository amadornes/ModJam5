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

public class SealPullLinear extends SealType {

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

    private static class Instance implements ISealInstance {

        private final ISeal seal;

        public Instance(ISeal seal) {
            this.seal = seal;
        }

        @Override
        public void update() {
            moveEntities(seal, true);
        }

    }

    public static void moveEntities(ISeal seal, boolean pull) {
        EnumFacing face = seal.getFace();
        Vec3d dv = new Vec3d(face.getDirectionVec()).scale(10);
        Vec3d center = new Vec3d(seal.getPos())
                .addVector(0.5, 0.5, 0.5)
                .add(new Vec3d(face.getOpposite().getDirectionVec()).scale(0.5));

        Collection<Entity> entities = seal.getWorld().getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(seal.getPos()).expand(dv.x, dv.y, dv.z));
        entities.forEach(e -> {
            Vec3d dir = e.getPositionVector().subtract(center).scale(pull ? -1 : 1).normalize().scale(0.1);
            e.motionX += dir.x;
            e.motionY += dir.y;
            e.motionZ += dir.z;
        });
    }

}
