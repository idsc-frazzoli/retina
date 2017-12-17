// code by jph
package ch.ethz.idsc.retina.gui.gokart.crv;

import java.util.Objects;

import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLcmClient;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseListener;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Tensor;

public class CurveFollowerModule extends AbstractClockedModule implements GokartPoseListener {
  private static final double PERIOD_S = 0.2;
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
      // TODO
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
