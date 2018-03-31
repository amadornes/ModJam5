package mod.crystals.seal;

import mod.crystals.api.seal.SealType;

import java.util.IdentityHashMap;
import java.util.Map;

public enum SealManager {
    INSTANCE;

    private final Map<SealType, SealType.Ingredient[][]> ingredients = new IdentityHashMap<>();

    public SealType.Ingredient[][] getIngredients(SealType seal){
        return ingredients.computeIfAbsent(seal, SealType::createRecipe);
    }

}
