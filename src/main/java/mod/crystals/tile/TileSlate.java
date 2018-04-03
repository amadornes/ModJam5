package mod.crystals.tile;

import mod.crystals.api.NatureType;
import mod.crystals.api.seal.SealType;
import mod.crystals.block.BlockSeal;
import mod.crystals.init.CrystalsRegistries;
import mod.crystals.util.SealUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class TileSlate extends TileEntity {

    private final Set<NatureType> natures = new HashSet<>();

    public boolean putDust(@Nullable NatureType type) {
        if (type == null) return false;
        if (natures.size() == 4) return false;
        if (getWorld().isRemote) {
            return !natures.contains(type);
        } else if (natures.add(type)) {
            IBlockState state = getWorld().getBlockState(getPos());
            getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
        return false;
    }

    public int getDustCount() {
        return natures.size();
    }

    public Set<NatureType> getNatures() {
        return natures;
    }

    public boolean tryForm() {
        TileSlate[][] slates = new TileSlate[3][3];
        if (!findSlates(slates)) return false;

        for (int i = 0; i < 4; i++) {
            SealType type = identifySeal(slates);
            if (type != null) {
                World world = getWorld();
                BlockPos pos = getPos();
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        getWorld().setBlockToAir(pos.add(x, 0, z));
                    }
                }
                Block.spawnAsEntity(world, pos, BlockSeal.createStack(type));
                return true;
            }
            if (i != 3) slates = rotate(slates);
        }

        return false;
    }

    private boolean findSlates(TileSlate[][] slates) {
        slates[1][1] = this;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                TileEntity te = getWorld().getTileEntity(getPos().add(x, 0, z));
                if (te instanceof TileSlate) {
                    slates[x + 1][z + 1] = (TileSlate) te;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private TileSlate[][] rotate(TileSlate[][] tiles) {
        TileSlate[][] slates = new TileSlate[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                slates[y][2 - x] = tiles[x][y];
            }
        }
        return slates;
    }

    private SealType identifySeal(TileSlate[][] slates) {
        for (SealType sealType : CrystalsRegistries.sealTypeRegistry) {
            SealType.Ingredient[][] ingredients = SealUtils.getIngredients(sealType);
            boolean failed = false;
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (ingredients[x][y] == null) {
                        if (!slates[x][y].natures.isEmpty()) {
                            failed = true;
                            break;
                        }
                    } else if (!ingredients[x][y].matches(slates[x][y].natures)) {
                        failed = true;
                        break;
                    }
                }
                if (failed) break;
            }
            if (!failed) {
                return sealType;
            }
        }
        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagList list = new NBTTagList();
        for (NatureType type : natures) {
            list.appendTag(new NBTTagString(type.getRegistryName().toString()));
        }
        compound.setTag("natures", list);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagList list = compound.getTagList("natures", Constants.NBT.TAG_STRING);
        natures.clear();
        for (int i = 0; i < list.tagCount(); i++) {
            // TODO: Handle missing natures
            natures.add(CrystalsRegistries.natureRegistry.getValue(new ResourceLocation(list.getStringTagAt(i))));
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        readFromNBT(tag);
        getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

}
