package mod.crystals.item;

import mod.crystals.api.NatureType;
import mod.crystals.init.CrystalsItems;
import mod.crystals.init.CrystalsRegistries;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class ItemDust extends ItemBase {

    public ItemDust() {
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) return;
        for (NatureType type : CrystalsRegistries.natureRegistry.getValuesCollection()) {
            items.add(getItemOfType(type));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "|" +
                Optional.ofNullable(getType(stack)).map(Impl::getRegistryName).orElse(null);
    }

    @Nullable
    public static NatureType getType(@Nonnull ItemStack stack) {
        if (stack.getItem() != CrystalsItems.dust) return null;
        if (!stack.hasTagCompound()) return null;
        String natureType = stack.getTagCompound().getString("nature_type");
        return CrystalsRegistries.natureRegistry.getValue(new ResourceLocation(natureType));
    }

    @Nonnull
    public static ItemStack getItemOfType(@Nonnull NatureType type) {
        ItemStack stack = new ItemStack(CrystalsItems.dust);
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("nature_type", type.getRegistryName().toString());
        return stack;
    }

}
