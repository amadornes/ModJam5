package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.api.NatureType;
import mod.crystals.api.seal.SealType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsRegistries {

    public static IForgeRegistry<NatureType> natureRegistry;
    public static IForgeRegistry<SealType> sealTypeRegistry;

    @SubscribeEvent
    public static void onRegistryCreation(RegistryEvent.NewRegistry event) {
        natureRegistry = new RegistryBuilder<NatureType>()
                .setType(NatureType.class)
                .setMaxID(256)
                .setName(new ResourceLocation(CrystalsMod.MODID, "nature"))
                .create();
        sealTypeRegistry = new RegistryBuilder<SealType>()
                .setType(SealType.class)
                .setMaxID(256)// TODO: Maybe expand
                .setName(new ResourceLocation(CrystalsMod.MODID, "seal_type"))
                .create();
    }

}
