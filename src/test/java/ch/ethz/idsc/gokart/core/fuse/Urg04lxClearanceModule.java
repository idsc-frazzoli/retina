// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** blocks rimo while anything is within a close distance in path
 * 
 * Important: The urg04lx lidar is not in use on the gokart.
 * The module has package visibility and may be removed in the future. */
/* package */ class Urg04lxClearanceModule extends AbstractModule implements //
    LidarRayBlockListener, RimoPutProvider, RimoGetListener {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private boolean isPathObstructed = true;
  // ---
  private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler(GokartLcmChannel.URG04LX_FRONT);
  private RimoGetEvent rimoGetEvent = null;

  @Override // from AbstractModule
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(this);
    urg04lxLcmHandler.lidarAngularFiringCollector.addListener(this);
    urg04lxLcmHandler.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
    urg04lxLcmHandler.stopSubscriptions();
  }

  /***************************************************/
  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    if (Objects.nonNull(rimoGetEvent))
      try {
        isPathObstructed = Urg04lxClearanceHelper.isPathObstructed(steerColumnInterface, lidarRayBlockEvent.floatBuffer);
        return;
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    isPathObstructed = true;
  }

  /***************************************************/
  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return Optional.ofNullable(isPathObstructed ? RimoPutEvent.PASSIVE : null);
  }
}
