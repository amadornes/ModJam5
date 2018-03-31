package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;

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

        public Instance(ISeal seal) {this.seal = seal;}

        @Override
        public void update() {
        }

    }

}
