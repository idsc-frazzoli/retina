// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.BSplineTrack;
import ch.ethz.idsc.gokart.core.map.TrackReconModule;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** module requires the TrackReconModule to provide the center line of an identified track */
public class CenterLinePursuitModule extends AbstractModule implements MPCBSplineTrackListener {
  /** in dubendorf resolution 100 yields points approx 0.5[m] apart.
   * resolution = 200 results in a spacing of ~0.25[m] */
  private static final int RESOLUTION = 200;
  // ---
  private final TrackReconModule trackReconModule = ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
  private final CurvePurePursuitModule curvePurePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  @Override
  protected void first() {
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersAdd(this);
    else
      System.err.println("did not subscribe to track info !!!");
    // ---
    curvePurePursuitModule.launch();
  }

  @Override
  protected void last() {
    curvePurePursuitModule.terminate();
    // ---
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersRemove(this);
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }

  @Override // from MPCBSplineTrackListener
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    Tensor curve = null;
    boolean closed = true;
    if (optional.isPresent()) {
      BSplineTrack bSplineTrack = optional.get().bSplineTrack();
      curve = bSplineTrack.getLineMiddle(RESOLUTION).map(Magnitude.METER);
      closed = bSplineTrack.isClosed();
      System.out.println("updated curve " + Dimensions.of(curve) + " closed=" + closed);
    } else {
      System.out.println("center line no waypoints");
    }
    curvePurePursuitModule.setCurve(Optional.ofNullable(curve), closed);
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(curve);
  }
}
