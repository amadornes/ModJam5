package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;

import java.util.function.BiConsumer;

public class SealPushRadial extends SealType {

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
            SealPullRadial.moveEntities(seal, false, this::consumeEnergy);
        }

    }

}
