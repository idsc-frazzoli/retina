// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;

public abstract class AbstractLidarMapping extends AbstractLidarProcessor implements GokartPoseListener {
  // TODO check rationale behind constant 10000!
  protected static final int LIDAR_SAMPLES = 10000;
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  protected GokartPoseEvent gokartPoseEvent;
  // ---
  protected final int waitMillis;
  protected final SpacialXZObstaclePredicate spacialXZObstaclePredicate;

  /* package */ AbstractLidarMapping(SpacialXZObstaclePredicate spacialXZObstaclePredicate, int waitMillis) {
    this.waitMillis = waitMillis;
    this.spacialXZObstaclePredicate = spacialXZObstaclePredicate;
  }

  @Override // from StartAndStoppable
  public final void start() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    super.start();
  }

  @Override // from StartAndStoppable
  public final void stop() {
    super.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
