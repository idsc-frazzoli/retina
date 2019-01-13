// code by jph
package ch.ethz.idsc.retina.lidar.app;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.LidarRotationEvent;
import ch.ethz.idsc.retina.lidar.LidarRotationListener;

public abstract class LidarPanoramaProvider implements LidarRayDataListener, LidarRotationListener {
  private final List<LidarPanoramaListener> lidarPanoramaListeners = new LinkedList<>();
  private final Supplier<LidarPanorama> supplier;
  protected LidarPanorama lidarPanorama;

  protected LidarPanoramaProvider(Supplier<LidarPanorama> supplier) {
    this.supplier = supplier;
    lidarPanorama = supplier.get();
  }

  public final void addListener(LidarPanoramaListener lidarPanoramaListener) {
    lidarPanoramaListeners.add(lidarPanoramaListener);
  }

  @Override
  public final void timestamp(int usec, int type) {
    // ---
  }

  @Override
  public final void lidarRotation(LidarRotationEvent lidarRotationEvent) {
    lidarPanoramaListeners.forEach(listener -> listener.lidarPanorama(lidarPanorama));
    lidarPanorama = supplier.get();
  }
}
