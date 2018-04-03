package mod.crystals.api.seal;

import mod.crystals.api.NatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SealType extends IForgeRegistryEntry.Impl<SealType> {

    public abstract Ingredient[][] createRecipe();

    public abstract ISealInstance instantiate(ISeal seal);

    public ResourceLocation getGlowyTextureLocation(TextureType type) {
        ResourceLocation name = getRegistryName();
        switch (type) {
            case GLOWY_BLACK:
                return new ResourceLocation(name.getResourceDomain(), "textures/seals/" + name.getResourcePath() + "/glowy_thing_black.png");
            case GLOWY_TRANSPARENT:
                return new ResourceLocation(name.getResourceDomain(), "textures/seals/" + name.getResourcePath() + "/glowy_thing.png");
        }
        return null;
    }

    public int getGlowyColor() {
        return 0xFFFFFF;
    }

    public enum TextureType {
        GLOWY_BLACK,
        GLOWY_TRANSPARENT;
    }

    public static final class Ingredient {

        private final Set<NatureType> natureTypes = new HashSet<>();

        public Ingredient(NatureType... natureTypes) {
            if (natureTypes.length > 4) {
                throw new IllegalArgumentException("An ingredient must have 4 nature types at most!");
            }
            this.natureTypes.addAll(Arrays.asList(natureTypes));
        }

        public boolean matches(Set<NatureType> natureTypes) {
            return natureTypes.size() == this.natureTypes.size() && natureTypes.containsAll(this.natureTypes);
        }

    }

}
