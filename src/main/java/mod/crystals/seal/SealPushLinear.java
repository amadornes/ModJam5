package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public class SealPushLinear extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient wind = new Ingredient(NatureType.AIR);
        Ingredient push = new Ingredient(NatureType.AIR, NatureType.DISTORTED);
        return new Ingredient[][]{
                {null, wind, null},
                {wind, push, wind},
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
            Vec3d dv = new Vec3d(seal.getFace().getDirectionVec()).scale(10);
            Vec3d dv1 = dv.normalize().scale(0.1);
            Collection<Entity> entities = seal.getWorld().getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(seal.getPos()).expand(dv.x, dv.y, dv.z));
            entities.forEach(e -> {
                e.motionX += dv1.x;
                e.motionY += dv1.y;
                e.motionZ += dv1.z;
            });
        }

    }

}