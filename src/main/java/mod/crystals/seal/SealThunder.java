package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.SealType;

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

}
