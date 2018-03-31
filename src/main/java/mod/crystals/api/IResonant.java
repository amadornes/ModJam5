package mod.crystals.api;

import gnu.trove.map.TObjectFloatMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public interface IResonant {

    @CapabilityInject(IResonant.class)
    Capability<IResonant> CAPABILITY = null;

    float getNatureAmount(NatureType natureType);

    float getResonance();

    void setNatureAmounts(TObjectFloatMap<NatureType> natureAmounts);

    void setResonance(float resonance);

    interface Default extends IResonant {

        TObjectFloatMap<NatureType> getNatureAmounts();

        int getColor();

        void addChangeListener(Runnable listener);

    }

}
