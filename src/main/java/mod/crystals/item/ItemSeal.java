package mod.crystals.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemSeal extends ItemBlock {
    public ItemSeal(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String suffix = "";
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("type")) suffix = "|" + tag.getString("type");
        return super.getUnlocalizedName(stack) + suffix;
    }
}
