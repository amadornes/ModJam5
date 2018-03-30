package mod.crystals.api;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Objects;

public class NatureType extends IForgeRegistryEntry.Impl<NatureType> {

    @GameRegistry.ObjectHolder("crystals:air")
    public static final NatureType AIR = null;
    @GameRegistry.ObjectHolder("crystals:water")
    public static final NatureType WATER = null;
    @GameRegistry.ObjectHolder("crystals:earth")
    public static final NatureType EARTH = null;
    @GameRegistry.ObjectHolder("crystals:fire")
    public static final NatureType FIRE = null;
    @GameRegistry.ObjectHolder("crystals:distorted")
    public static final NatureType DISTORTED = null;
    @GameRegistry.ObjectHolder("crystals:void")
    public static final NatureType VOID = null;

    private final int color;

    public NatureType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return Objects.toString(getRegistryName());
    }

}
