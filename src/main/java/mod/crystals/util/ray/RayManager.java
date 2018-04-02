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
    private Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create();

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

        Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create(this.rays);
        rays.put(from, to, ray);
        this.rays = rays;

        from.onConnect(to, ray);
        to.onConnect(from, ray);

        return ray;
    }

    public void removeRay(ILaserSource src1, ILaserSource src2) {
        Ray ray;

        Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create(this.rays);

        ray = rays.remove(src1, src2);
        if (ray != null) {
            src1.onDisconnect(src2, ray);
            src2.onDisconnect(src1, ray);
            return;
        }

        ray = rays.remove(src2, src1);
        if (ray != null) {
            src1.onDisconnect(src2, ray);
            src2.onDisconnect(src1, ray);
        }

        this.rays = rays;
    }

    public void removeAll(ILaserSource src) {
        rays.row(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray);
        });
        rays.column(src).forEach((to, ray) -> {
            src.onDisconnect(to, ray);
            to.onDisconnect(src, ray);
        });

        Table<ILaserSource, ILaserSource, Ray> rays = HashBasedTable.create(this.rays);
        rays.rowKeySet().remove(src);
        rays.columnKeySet().remove(src);
        this.rays = rays;
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
        for (Ray ray : getRays()) { // Avoid CMEs... Yay
            ray.update(world);
        }
    }

}
