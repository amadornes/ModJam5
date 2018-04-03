package mod.crystals.item;

import mod.crystals.api.seal.SealType;
import mod.crystals.block.BlockSeal;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSeal extends ItemBlock {
    public static int sealSize = -1;

    public ItemSeal(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        SealType type = BlockSeal.getType(stack);
        sealSize = type.getSize();
        EnumActionResult result = super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        sealSize = -1;
        return result;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        SealType type = BlockSeal.getType(stack);
        sealSize = type.getSize();
        boolean result = super.canPlaceBlockOnSide(worldIn, pos, side, player, stack);
        sealSize = -1;
        return result;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String suffix = "";
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("type")) suffix = "|" + tag.getString("type");
        return super.getUnlocalizedName(stack) + suffix;
    }
}
