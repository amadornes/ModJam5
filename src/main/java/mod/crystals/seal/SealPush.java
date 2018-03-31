package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.SealType;

public class SealPush extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient wind = new Ingredient(NatureType.AIR);
        return new Ingredient[][]{
            {wind, wind, wind},
            {wind, wind, wind},
            {wind, wind, wind}
        };
    }

}
