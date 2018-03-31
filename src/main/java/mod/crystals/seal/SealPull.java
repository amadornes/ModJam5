package mod.crystals.seal;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.SealType;

public class SealPull extends SealType {

    @Override
    public Ingredient[][] createRecipe() {
        Ingredient wind = new Ingredient(NatureType.AIR);
        Ingredient pull = new Ingredient(NatureType.AIR, NatureType.VOID);
        return new Ingredient[][]{
            {wind, wind, wind},
            {wind, pull, wind},
            {wind, wind, wind}
        };
    }

}
