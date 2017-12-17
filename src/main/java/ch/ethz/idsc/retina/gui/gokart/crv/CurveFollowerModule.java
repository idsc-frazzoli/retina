// code by jph
package ch.ethz.idsc.retina.gui.gokart.crv;

import java.util.Objects;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLcmClient;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseListener;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.curve.PeriodicExtract;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class CurveFollowerModule extends AbstractClockedModule implements GokartPoseListener {
  public static final Tensor CURVE = DubendorfCurve.OVAL;
  // ---
  private static final double PERIOD_S = 0.2; // 0.2[s]
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final CurveFollowerSteer curveFollowerSteer = new CurveFollowerSteer();
  private final CurveFollowerRimo curveFollowerRimo = new CurveFollowerRimo();
  // ---
  private GokartPoseEvent gokartPoseEvent = null;

  @Override // from AbstractModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    curveFollowerRimo.start();
    curveFollowerSteer.start();
  }

  @Override // from AbstractModule
  protected void last() {
    curveFollowerRimo.stop();
    curveFollowerSteer.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    if (Objects.nonNull(gokartPoseEvent)) {
      Tensor pose = gokartPoseEvent.getPose(); // latest pose
      Tensor poseNoUnits = pose.map(scalar -> RealScalar.of(scalar.number()));
      Se2Bijection se2Bijection = new Se2Bijection(poseNoUnits);
      TensorUnaryOperator tensorUnaryOperator = se2Bijection.inverse();
      Tensor beacons = Tensor.of(CURVE.stream().map(tensorUnaryOperator));
      int index = CurveUtils.closestCloserThan(beacons, RealScalar.of(1.5));
      boolean status = 0 <= index;
      if (status) {
        PeriodicExtract periodicExtract = new PeriodicExtract(beacons);
        periodicExtract.get(index);
      }
      curveFollowerSteer.setOperational(status);
      curveFollowerRimo.setOperational(status);
    }
  }

  @Override // from AbstractClockedModule
  protected double getPeriod() {
    return PERIOD_S;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 50[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
