// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** module requires the GokartTrackReconModule to provide the center line of an identified track */
public class CenterLinePursuitModule extends AbstractModule implements MPCBSplineTrackListener {
  /** in dubendorf resolution 100 yields points approx 0.5[m] apart.
   * resolution = 200 results in a spacing of ~0.25[m] */
  private static final int RESOLUTION = 200;
  // ---
  private final GokartTrackReconModule gokartTrackReconModule = //
      ModuleAuto.INSTANCE.getInstance(GokartTrackReconModule.class);
  private final CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  @Override
  protected void first() throws Exception {
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.listenersAdd(this);
    // ---
    purePursuitModule.launch();
  }

  @Override
  protected void last() {
    purePursuitModule.terminate();
    // ---
    if (Objects.nonNull(gokartTrackReconModule))
      gokartTrackReconModule.listenersRemove(this);
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }

  @Override // from MPCBSplineTrackListener
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    Tensor curve = null;
    if (optional.isPresent()) {
      curve = optional.get().bSplineTrack().getLineMiddle(RESOLUTION).map(Magnitude.METER);
      System.out.println("updated curve " + Dimensions.of(curve));
    } else {
      System.out.println("center line no waypoints");
    }
    purePursuitModule.setCurve(Optional.ofNullable(curve));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(curve);
  }
}
