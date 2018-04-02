package mod.crystals.tile;

import mod.crystals.api.IResonant;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class TileCrystal extends TileCrystalBase {

    public TileCrystal(boolean ignoreJoin) {
        super((IResonant.Default) IResonant.CAPABILITY.getDefaultInstance(), ignoreJoin);
    }

    public TileCrystal() {
        super((IResonant.Default) IResonant.CAPABILITY.getDefaultInstance());
        resonant.addChangeListener(this::onChanged);
    }

    @Override
    public Vec3d getPosition(float partialTicks) {
        if (getBlockMetadata() == 0) {
            BlockPos pos = getPos();
            double time = getWorld().getTotalWorldTime() + partialTicks + (pos.getX() ^ pos.getY() ^ pos.getZ());
            float off = (int) (time % 80) / 80F;
            return OFFSET.add(new Vec3d(getPos())).addVector(0, 0.25 + 0.03125 * Math.sin(Math.PI * off * 2), 0);
        } else {
            return OFFSET.add(new Vec3d(getPos()));
        }
    }

    @Override
    public Vec3d getColor(float partialTicks) {
        Color color = new Color(resonant.getColor());
        return new Vec3d(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTBase tag = IResonant.CAPABILITY.writeNBT(resonant, null);
        compound.setTag("rd", tag);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTBase tag = compound.getTag("rd");
        if (tag != null) IResonant.CAPABILITY.readNBT(resonant, null, tag);
    }

}
