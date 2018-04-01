package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;

public class SealPushRadial extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient wind = new Ingredient(NatureType.AIR);
        Ingredient push = new Ingredient(NatureType.AIR, NatureType.DISTORTED);
        return new Ingredient[][]{
                {wind, wind, wind},
                {wind, push, wind},
                {wind, wind, wind}
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
            SealPullRadial.moveEntities(seal, false);
        }

    }

}
