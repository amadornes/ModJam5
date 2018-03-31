package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.SealType;

public class SealLocalRain extends SealType {

    private Ingredient[][] INGREDIENTS;

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
