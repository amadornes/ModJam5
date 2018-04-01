package mod.crystals.item;

import mod.crystals.creativetab.CreativeTabCrystals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBase extends Item {

    public ItemBase() {
        super();
        setCreativeTab(CreativeTabCrystals.instance);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes()) {
            return super.getUnlocalizedName(stack) + "|" + stack.getMetadata();
        } else {
            return super.getUnlocalizedName(stack);
        }
    }

}
