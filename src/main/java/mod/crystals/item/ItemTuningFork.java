package mod.crystals.item;

import gnu.trove.map.TObjectFloatMap;
import mod.crystals.api.IResonant;
import mod.crystals.api.NatureType;
import mod.crystals.environment.EnvironmentHandler;
import mod.crystals.util.ResonantUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTuningFork extends ItemBase {

    private static final float ENVIRONMENT_TUNING_RATE = 0.005F;
    private static final float CRYSTAL_TUNING_RATE = 0.005F;

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isRemote) return;
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (player.getHeldItem(EnumHand.MAIN_HAND) == stack || player.getHeldItem(EnumHand.OFF_HAND) == stack) {
            energizeFork(stack);
            if (balanceCrystals(stack, world, player)) return;
            balanceFork(stack, world, player);
        }
    }

    private void energizeFork(ItemStack stack) {
        IResonant resonant = stack.getCapability(IResonant.CAPABILITY, null);
        if (resonant.getResonance() < 1) {
            resonant.setResonance(Math.min(resonant.getResonance() + ENVIRONMENT_TUNING_RATE, 1));
        }
    }

    private void balanceFork(ItemStack stack, World world, EntityPlayer player) {
        IResonant resonant = stack.getCapability(IResonant.CAPABILITY, null);
        TObjectFloatMap<NatureType> itemNatures = ResonantUtils.getNatureTypes(resonant, true);
        TObjectFloatMap<NatureType> worldNatures = EnvironmentHandler.INSTANCE.getNature(world, player.getPosition());

        ResonantUtils.balance(itemNatures, worldNatures, resonant.getResonance(), 1, ENVIRONMENT_TUNING_RATE);
        resonant.setNatureAmounts(itemNatures);
    }

    private boolean balanceCrystals(ItemStack stack, World world, EntityPlayer player) {
        IResonant resonant = stack.getCapability(IResonant.CAPABILITY, null);
        TObjectFloatMap<NatureType> itemNatures = ResonantUtils.getNatureTypes(resonant, true);
        float itemRes = resonant.getResonance();
        boolean found = false;

        AxisAlignedBB extendedPlayerAABB = player.getEntityBoundingBox().grow(2);
        BlockPos min = new BlockPos(extendedPlayerAABB.minX, extendedPlayerAABB.minY, extendedPlayerAABB.minZ);
        BlockPos max = new BlockPos(extendedPlayerAABB.maxX, extendedPlayerAABB.maxY, extendedPlayerAABB.maxZ);

        for (BlockPos p : BlockPos.getAllInBoxMutable(min, max)) {
            IResonant crystal = ResonantUtils.getCrystal(world, p);
            if (crystal == null) continue;

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

            found = true;
        }

        resonant.setNatureAmounts(itemNatures);
        return found;
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
