package mod.crystals.util;

import mod.crystals.api.seal.SealType;

import java.util.IdentityHashMap;
import java.util.Map;

public class SealUtils {

    private static final Map<SealType, SealType.Ingredient[][]> ingredients = new IdentityHashMap<>();

    public static SealType.Ingredient[][] getIngredients(SealType seal){
        return ingredients.computeIfAbsent(seal, SealType::createRecipe);
    }

}
