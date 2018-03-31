package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public class SealThunder extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient cloud = new Ingredient(NatureType.WATER, NatureType.AIR, NatureType.DISTORTED);
        Ingredient rain = new Ingredient(NatureType.WATER, NatureType.AIR);
        Ingredient thunder = new Ingredient(NatureType.FIRE, NatureType.AIR, NatureType.DISTORTED);
        return new Ingredient[][]{
            {cloud, cloud, cloud},
            {rain, thunder, rain},
            {rain, thunder, rain}
        };
    }

    @Override
    public ISealInstance instantiate(ISeal seal) {
        return new Instance(seal);
    }

    private static class Instance implements ISealInstance {

        private final ISeal seal;

        public Instance(ISeal seal) {this.seal = seal;}

        @Override
        public void update() {
        }

    }

}
