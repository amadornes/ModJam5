package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.SealType;

public class SealRain extends SealType {

    private Ingredient[][] INGREDIENTS;

    @Override
    public Ingredient[][] getIngredients() {
        if (INGREDIENTS == null) {
            Ingredient cloud = new Ingredient(NatureType.WATER, NatureType.AIR, NatureType.DISTORTED);
            Ingredient rain = new Ingredient(NatureType.WATER, NatureType.AIR);
            INGREDIENTS = new Ingredient[][]{
                    {cloud, cloud, cloud},
                    {rain, rain, rain},
                    {rain, rain, rain}
            };
        }
        return INGREDIENTS;
    }

}
