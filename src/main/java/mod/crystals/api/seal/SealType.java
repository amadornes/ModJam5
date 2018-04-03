package mod.crystals.api.seal;

import mod.crystals.api.NatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class SealType extends IForgeRegistryEntry.Impl<SealType> {

    /**
     * Seal size. 0 = 1 block volume, 1 = 9 blocks, 2 = 25 blocks, …
     */
    public abstract int getSize();

    public ResourceLocation getTextureLocation(TextureType type) {
        int size = getSize();
        if (size == 0 && (type == TextureType.BASE || type == TextureType.OVERLAY)) {
            return getTextureName(type);
        } else if (size == 1 && (type == TextureType.GLOW_BLACK || type == TextureType.GLOW_TRANSPARENT)) {
            return getTextureName(type);
        }
        return null;
    }

    protected ResourceLocation getTextureName(TextureType type) {
        if (type == TextureType.BASE) {
            return new ResourceLocation("crystals", "textures/seals/base/small.png");
        }
        ResourceLocation name = getRegistryName();
        return new ResourceLocation(name.getResourceDomain(), "textures/seals/" + name.getResourcePath() + "/" + type.getName() + ".png");
    }

    public float getRotationSpeed() {
        return getSize() == 0 ? 0 : 1;
    }

    public int getTint() {
        return 0xFFFFFF;
    }

    public abstract Ingredient[][] createRecipe();

    public abstract ISealInstance instantiate(ISeal seal);

    public enum TextureType {
        BASE("small"),
        OVERLAY("overlay"),
        GLOW_BLACK("glow_black"),
        GLOW_TRANSPARENT("glow");

        private final String name;

        TextureType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

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
