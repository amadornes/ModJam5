package mod.crystals.item;

import gnu.trove.map.TObjectFloatMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.block.BlockCrystal;
import mod.crystals.environment.EnvironmentHandler;
import mod.crystals.init.CrystalsBlocks;
import mod.crystals.tile.TileCrystal;
import mod.crystals.tile.TileCrystalBase;
import mod.crystals.util.ResonantUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ItemTuningFork extends ItemBase {

    private static final float ENVIRONMENT_TUNING_RATE = 0.005F;
    private static final float CRYSTAL_TUNING_RATE = 0.005F;

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) entity;

        int prevMeta = getMetadata(stack);
        EnumHand hand = null;
        if (player.getHeldItem(EnumHand.MAIN_HAND) == stack) hand = EnumHand.MAIN_HAND;
        else if (player.getHeldItem(EnumHand.OFF_HAND) == stack) hand = EnumHand.OFF_HAND;

        if (hand != null) {
            balanceFork(stack, world, player);
            balanceCrystals(stack, world, player);

            if (prevMeta != getMetadata(stack)) {
                player.sendContainerToPlayer(player.inventoryContainer);
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileCrystal)) return EnumActionResult.PASS;

        ItemStack stack = player.getHeldItem(hand);
        IResonant resonantItem = stack.getCapability(IResonant.CAPABILITY, null);
        IResonant resonantCrystal = te.getCapability(IResonant.CAPABILITY, null);
        if (resonantCrystal == null) return EnumActionResult.PASS;

        float match = ResonantUtils.getMatch(resonantItem, resonantCrystal);
        if (match < 0.8) return EnumActionResult.FAIL;

        ItemStack block = new ItemStack(CrystalsBlocks.crystal);
        NBTTagCompound tileData = new NBTTagCompound();
        te.writeToNBT(tileData);
        tileData.removeTag("wg");
        if (!block.hasTagCompound()) block.setTagCompound(new NBTTagCompound());
        block.getTagCompound().setTag("BlockEntityTag", tileData);
        Block.spawnAsEntity(worldIn, pos, block);
        BlockCrystal.dontDropItems = true;
        worldIn.setBlockToAir(pos);
        BlockCrystal.dontDropItems = false;

        return EnumActionResult.SUCCESS;
    }

    private void balanceFork(ItemStack stack, World world, EntityPlayer player) {
        IResonant resonant = stack.getCapability(IResonant.CAPABILITY, null);

        if (resonant.getResonance() < 1) {
            resonant.setResonance(Math.min(resonant.getResonance() + ENVIRONMENT_TUNING_RATE, 1));
        }

        TObjectFloatMap<NatureType> itemNatures = ResonantUtils.getNatureTypes(resonant, true);
        TObjectFloatMap<NatureType> worldNatures = EnvironmentHandler.INSTANCE.getNature(world, player.getPosition());

        ResonantUtils.balance(itemNatures, worldNatures, resonant.getResonance(), 1, ENVIRONMENT_TUNING_RATE);
        resonant.setNatureAmounts(itemNatures);
    }

    private void balanceCrystals(ItemStack stack, World world, EntityPlayer player) {
        IResonant resonant = stack.getCapability(IResonant.CAPABILITY, null);
        TObjectFloatMap<NatureType> itemNatures = ResonantUtils.getNatureTypes(resonant, true);
        float itemRes = resonant.getResonance();

        Set<TileCrystalBase> crystals = ResonantUtils.getCrystalsAround(world, player.getPosition(), 2, null);
        for (TileCrystalBase te : crystals) {
            IResonant crystal = te.getCapability(IResonant.CAPABILITY, null);

            TObjectFloatMap<NatureType> crystalNatures = ResonantUtils.getNatureTypes(crystal, true);
            float crystalRes = crystal.getResonance();

            ResonantUtils.balance(itemNatures, crystalNatures, itemRes, crystalRes, CRYSTAL_TUNING_RATE);

            float resDif = Math.min(Math.abs(crystalRes - itemRes), 0.01F) / 2F;
            if (crystalRes < itemRes) {
                crystal.setResonance(crystalRes + resDif);
                itemRes -= resDif;
            } else {
                crystal.setResonance(crystalRes - resDif);
                itemRes += resDif;
            }

            crystal.setNatureAmounts(crystalNatures);
        }

        resonant.setNatureAmounts(itemNatures);
    }

    @Override
    public int getMetadata(ItemStack stack) {
        return stack.getCapability(IResonant.CAPABILITY, null).getResonance() > 0.2 ? 1 : 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        IResonant resonant = IResonant.CAPABILITY.getDefaultInstance();
        return new ICapabilitySerializable() {

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == IResonant.CAPABILITY;
            }

            @Nullable
            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == IResonant.CAPABILITY ? (T) resonant : null;
            }

            @Override
            public NBTBase serializeNBT() {
                return IResonant.CAPABILITY.writeNBT(resonant, null);
            }

            @Override
            public void deserializeNBT(NBTBase nbt) {
                IResonant.CAPABILITY.readNBT(resonant, null, nbt);
            }

        };
    }

}
