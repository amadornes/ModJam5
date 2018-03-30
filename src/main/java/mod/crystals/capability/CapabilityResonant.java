package mod.crystals.capability;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.init.CrystalsRegistries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.annotation.Nullable;
import java.awt.*;

public class CapabilityResonant {

    public static class Storage implements Capability.IStorage<IResonant> {

        @Nullable
        @Override
        public NBTBase writeNBT(Capability<IResonant> capability, IResonant obj, EnumFacing side) {
            if (!(obj instanceof DefaultImpl)) {
                throw new IllegalArgumentException("Cannot serialize an object of type " + obj.getClass().getName());
            }

            DefaultImpl impl = (DefaultImpl) obj;
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound natures = new NBTTagCompound();
            impl.natures.forEachEntry((nature, amt) -> {
                natures.setFloat(nature.getRegistryName().toString(), amt);
                return true;
            });
            tag.setTag("natures", natures);
            tag.setFloat("resonance", impl.resonance);
            return tag;
        }

        @Override
        public void readNBT(Capability<IResonant> capability, IResonant obj, EnumFacing side, NBTBase nbt) {
            if (!(obj instanceof DefaultImpl)) {
                throw new IllegalArgumentException("Cannot deserialize an object of type " + obj.getClass().getName());
            }

            DefaultImpl impl = (DefaultImpl) obj;
            NBTTagCompound tag = (NBTTagCompound) nbt;
            NBTTagCompound natures = tag.getCompoundTag("natures");
            impl.natures.clear();
            for (ResourceLocation name : CrystalsRegistries.natureRegistry.getKeys()) {
                NatureType natureType = CrystalsRegistries.natureRegistry.getValue(name);
                impl.natures.put(natureType, natures.getFloat(name.toString()));
            }
            impl.resonance = tag.getFloat("resonance");
        }

    }

    public static class DefaultImpl implements IResonant.Default {

        private final TObjectFloatMap<NatureType> natures = new TObjectFloatHashMap<>(16, 0.75F, 0);
        private float resonance = 0;

        public DefaultImpl() {
            natures.put(NatureType.DISTORTED, 1);
        }

        @Override
        public float getNatureAmount(NatureType natureType) {
            if (resonance == 0) return 0;
            return natures.get(natureType);
        }

        @Override
        public float getResonance() {
            return resonance;
        }

        @Override
        public TObjectFloatMap<NatureType> getNatureAmounts() {
            return natures;
        }

        @Override
        public int getColor() {
            float def = 127 * (1 - resonance);
            float red = def, green = def, blue = def;
            for (NatureType type : natures.keySet()) {
                float amt = natures.get(type) * resonance;
                Color color = new Color(type.getColor());
                red += color.getRed() * amt;
                green += color.getGreen() * amt;
                blue += color.getBlue() * amt;
            }
            return (((int) Math.floor(red)) << 16) | (((int) Math.floor(green)) << 8) | ((int) Math.floor(blue));
        }

        @Override
        public void setNatureAmounts(TObjectFloatMap<NatureType> natureAmounts) {
            float sum = 0;
            for (float amt : natureAmounts.values()) {
                sum += amt;
            }
            if (Math.abs(sum - 1) < 1e-4) {
                throw new IllegalArgumentException("The provided natures don't add up to 1, they add up to " + sum + "!");
            }
            natures.clear();
            natures.putAll(natureAmounts);
        }

        @Override
        public void setResonance(float res) {
            if (res < 1e-4) {
                resonance = 0;
                natures.clear();
                natures.put(NatureType.DISTORTED, 1);
                return;
            }
            resonance = res;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                    .append("natures", natures)
                    .append("resonance", resonance)
                    .toString();
        }

    }

}
