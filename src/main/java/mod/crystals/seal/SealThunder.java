package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;

import java.util.function.BiConsumer;

public class SealThunder extends SealType {

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public int getTint() {
        return NatureType.WATER.getColor();
    }

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

    private static class Instance extends AbstractSeal {

        public Instance(ISeal seal) {
            super(seal);
        }

        @Override
        public void addRequirements(BiConsumer<NatureType, Float> capacity, BiConsumer<NatureType, Float> consumption) {
            capacity.accept(NatureType.WATER, 5000F);
            capacity.accept(NatureType.AIR, 5000F);
            capacity.accept(NatureType.FIRE, 2500F);
            capacity.accept(NatureType.DISTORTED, 2500F);
            consumption.accept(NatureType.WATER, 5000F);
            consumption.accept(NatureType.AIR, 5000F);
            consumption.accept(NatureType.FIRE, 2500F);
            consumption.accept(NatureType.DISTORTED, 2500F);
        }

        @Override
        public void update() {
            SealRain.startRain(seal, true, 20 * 60 * 5, this::consumeEnergy);
        }

    }

}
