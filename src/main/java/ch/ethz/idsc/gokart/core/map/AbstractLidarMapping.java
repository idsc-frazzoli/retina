package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;

public abstract class AbstractLidarMapping extends AbstractLidarProcessor implements GokartPoseListener {
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  protected GokartPoseEvent gokartPoseEvent;
  // ---
  protected final SpacialXZObstaclePredicate predicate;

  /* package */ AbstractLidarMapping(SpacialXZObstaclePredicate predicate, int waitMillis) {
    super(waitMillis);
    this.predicate = predicate;
    gokartPoseLcmClient.addListener(this);
  }

  @Override // from StartAndStoppable
  public void start() {
    gokartPoseLcmClient.startSubscriptions();
    super.start();
  }

  @Override // from StartAndStoppable
  public void stop() {
    super.stop();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
