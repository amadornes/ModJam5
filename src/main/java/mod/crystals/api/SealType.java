package mod.crystals.api;

import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SealType extends IForgeRegistryEntry.Impl<SealType> {

    public abstract Ingredient[][] getIngredients();

    public static final class Ingredient {

        private final Set<NatureType> natureTypes = new HashSet<>();

        public Ingredient(NatureType... natureTypes) {
            if (natureTypes.length > 4) {
                throw new IllegalArgumentException("An ingredient must have 4 nature types at most!");
            }
            this.natureTypes.addAll(Arrays.asList(natureTypes));
        }

        public boolean matches(Set<NatureType> natureTypes) {
            return natureTypes.containsAll(this.natureTypes);
        }

    }

}
