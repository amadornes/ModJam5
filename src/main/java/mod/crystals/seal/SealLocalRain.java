package mod.crystals.seal;

import mod.crystals.api.NatureType;
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

}
