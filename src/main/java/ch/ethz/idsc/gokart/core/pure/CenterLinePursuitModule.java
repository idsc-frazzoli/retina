// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.gui.top.GlobalViewLcmModule;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** module requires the GokartTrackReconModule to provide the center line of an identified track */
public class CenterLinePursuitModule extends AbstractModule {
  private static final int RESOLUTION = 200; // TODO magic const
  private final GokartTrackReconModule gokartTrackReconModule = //
      ModuleAuto.INSTANCE.getInstance(GokartTrackReconModule.class);
  private final CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule(PursuitConfig.GLOBAL);
  private final GlobalViewLcmModule globalViewLcmModule = ModuleAuto.INSTANCE.getInstance(GlobalViewLcmModule.class);

  public CenterLinePursuitModule() {
    Tensor curve = null;
    if (Objects.nonNull(gokartTrackReconModule)) {
      MPCBSplineTrack mpcBSplineTrack = gokartTrackReconModule.getMPCBSplineTrack();
      if (Objects.nonNull(mpcBSplineTrack)) {
        curve = mpcBSplineTrack.bSplineTrack.getLineMiddle(RESOLUTION).map(Magnitude.METER);
        System.out.println("curve set as: " + Dimensions.of(curve));
        System.out.println("first point : " + curve.get(0));
      }
    }
    purePursuitModule.setCurve(Optional.ofNullable(curve));
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(curve);
  }

  @Override
  protected void first() throws Exception {
    purePursuitModule.launch();
  }

  @Override
  protected void last() {
    purePursuitModule.terminate();
    if (Objects.nonNull(globalViewLcmModule))
      globalViewLcmModule.setCurve(null);
  }
}
