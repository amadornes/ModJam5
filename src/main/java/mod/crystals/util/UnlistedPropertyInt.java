package mod.crystals.util;

import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Objects;

public class UnlistedPropertyInt implements IUnlistedProperty<Integer> {

    private final String name;

    public UnlistedPropertyInt(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Integer value) {
        return value != null;
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String valueToString(Integer value) {
        return Objects.toString(value);
    }

}
