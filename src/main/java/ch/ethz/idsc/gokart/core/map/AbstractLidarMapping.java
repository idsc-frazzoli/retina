// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;

public abstract class AbstractLidarMapping extends AbstractLidarProcessor implements GokartPoseListener {
  // TODO JG check rationale behind constant 10000!
  protected static final int LIDAR_SAMPLES = 10000;
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  /** implementations are encouraged to test quality of pose before using coordinate */
  protected GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
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
