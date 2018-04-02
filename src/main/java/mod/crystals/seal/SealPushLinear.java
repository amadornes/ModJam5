package mod.crystals.seal;

import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.ISeal;
import mod.crystals.api.seal.ISealInstance;
import mod.crystals.api.seal.SealType;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;

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
    public ResourceLocation getGlowyTextureLocation(TextureType type) {
        switch (type) {
            case GLOWY_BLACK:
                return new ResourceLocation(CrystalsMod.MODID, "textures/seals/push_linear/glowy_thing_black.png");
            case GLOWY_TRANSPARENT:
                return new ResourceLocation(CrystalsMod.MODID, "textures/seals/push_linear/glowy_thing.png");
            case GLOWY_SHIMMER:
                //return new ResourceLocation(CrystalsMod.MODID, "textures/seals/pushpull/glowy_thing_shimmer.png");
        }
        return null;
    }

    @Override
    public int getGlowyColor() {
        return NatureType.AIR.getColor();
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
            capacity.accept(NatureType.AIR, 200F);
            consumption.accept(NatureType.AIR, 50F);
        }

        @Override
        public void update() {
            SealPullLinear.moveEntities(seal, false, 1.5F, this::consumeEnergy);
        }

    }

}
