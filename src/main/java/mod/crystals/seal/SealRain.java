package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.SealType;

public class SealRain extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient cloud = new Ingredient(NatureType.WATER, NatureType.AIR, NatureType.DISTORTED);
        Ingredient rain = new Ingredient(NatureType.WATER, NatureType.AIR);
        return new Ingredient[][]{
                {cloud, cloud, cloud},
                {rain, rain, rain},
                {rain, rain, rain}
        };
    }

}
