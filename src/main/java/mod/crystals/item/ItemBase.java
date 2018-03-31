package mod.crystals.item;

import mod.crystals.creativetab.CreativeTabCrystals;
import net.minecraft.item.Item;

public class ItemBase extends Item {

    public ItemBase() {
        super();
        setCreativeTab(CreativeTabCrystals.instance);
    }

}
