package mod.crystals.init;

import mod.crystals.CrystalsMod;
import mod.crystals.item.ItemDust;
import mod.crystals.item.ItemTuningFork;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = CrystalsMod.MODID)
public class CrystalsItems {

    public static final Item tuning_fork = new ItemTuningFork();
    public static final Item dust = new ItemDust();

    @SubscribeEvent
    public static void onItemRegistration(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registerItem(registry, tuning_fork, "tuning_fork");
        registerItem(registry, dust, "dust");
    }

    private static void registerItem(IForgeRegistry<Item> registry, Item item, String name) {
        ResourceLocation rl = new ResourceLocation(CrystalsMod.MODID, name);
        item.setRegistryName(rl);
        item.setUnlocalizedName(rl.toString());
        registry.register(item);
    }

}
