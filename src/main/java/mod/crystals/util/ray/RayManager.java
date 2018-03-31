package mod.crystals.util.ray;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import mod.crystals.util.ILaserSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.ref.WeakReference;
import java.util.Collection;

public class RayManager {

    private final WeakReference<World> world;
    private final Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create();

    public RayManager(World world) {
        this.world = new WeakReference<>(world);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Ray addRay(ILaserSource from, ILaserSource to) {
        Ray ray = rays.get(from, to);
        if (ray != null) return ray;

        ray = rays.get(to, from);
        if (ray != null) return ray.getOpposite();

        ray = new Ray(from, to);
        rays.put(from, to, ray);
        return ray;
    }

    public void removeRay(ILaserSource src1, ILaserSource src2) {
        rays.remove(src1, src2);
        rays.remove(src2, src1);
    }

    public void removeAll(ILaserSource src) {
        rays.rowKeySet().remove(src);
        rays.columnKeySet().remove(src);
    }

    public Collection<Ray> getRays() {
        return rays.values();
    }

    // TODO: Don't do this. Please... No... Use the block placed event and check every couple seconds just in case...

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (world.get() == null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if (event.phase == TickEvent.Phase.START || event.world != world.get()) return;

        for (Ray ray : getRays()) {
            ray.update(event.world);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (world.get() == null) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        if (event.phase == TickEvent.Phase.START) return;
        if (Minecraft.getMinecraft().isGamePaused()) return;

        World world = this.world.get();
        for (Ray ray : getRays()) {
            ray.update(world);
        }
    }

}
