package mod.crystals.creativetab;

import mod.crystals.init.CrystalsBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeTabCrystals extends CreativeTabs {
    public static final CreativeTabs instance = new CreativeTabCrystals();

    private CreativeTabCrystals() {
        super("crystals");
    }

    @Override
    @Nonnull
    public ItemStack getTabIconItem() {
        return new ItemStack(CrystalsBlocks.crystal);
    }

    public static void init() {}
}
