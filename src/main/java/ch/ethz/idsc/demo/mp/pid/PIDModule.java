// code by mcp (used CenterLinePursuiteModule by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.TrackReconModule;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** module requires the TrackReconModule to provide the center line of an
 * identified track */
public class PIDModule extends AbstractModule implements MPCBSplineTrackListener {
  private static final int RESOLUTION = 200;
  private final TrackReconModule trackReconModule = ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
  private final PIDControllerModule pidControllerModule = new PIDControllerModule(PIDTuningParams.GLOBAL);

  @Override // from AbstractModule
  protected void first() {
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersAdd(this);
    else
      System.err.println("no track info");
    pidControllerModule.launch();
  }

  @Override // from AbstractModule
  protected void last() {
    pidControllerModule.terminate();
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersRemove(this);
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
    pidControllerModule.setCurve(Optional.ofNullable(curve));
  }
}