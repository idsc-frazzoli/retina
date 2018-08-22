// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** container for the objects that are modified by the SLAM algorithm */
// TODO MG initialization for localization mode
public class SlamContainer {
  private final SlamEstimatedPose slamEstimatedPose;
  private final SlamParticle[] slamParticles;
  private final MapProvider occurrenceMap;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  // ---
  private List<SlamWayPoint> wayPoints;
  private double[] eventGokartFrame;
  private boolean active;

  public SlamContainer(SlamConfig slamConfig) {
    slamEstimatedPose = new SlamEstimatedPose();
    int numOfPart = slamConfig.numberOfParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int index = 0; index < numOfPart; ++index)
      slamParticles[index] = new SlamParticle();
    occurrenceMap = new MapProvider(slamConfig);
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig.linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig.linVelStd);
    angVelStd = Magnitude.PER_SECOND.toDouble(slamConfig.angVelStd);
    wayPoints = new ArrayList<>();
    eventGokartFrame = null;
  }

  /** @param initPose {x[m], y[m], angle[-]} */
  public void initialize(Tensor initPose) {
    active = true;
    SlamContainerUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    slamEstimatedPose.setPose(initPose);
  }

  public SlamParticle[] getSlamParticles() {
    return slamParticles;
  }

  public MapProvider getOccurrenceMap() {
    return occurrenceMap;
  }

  public SlamEstimatedPose getSlamEstimatedPose() {
    return slamEstimatedPose;
  }

  public boolean getActive() {
    return active;
  }

  public void setWayPoints(List<SlamWayPoint> wayPoints) {
    this.wayPoints = wayPoints;
  }

  public List<SlamWayPoint> getWayPoints() {
    return wayPoints;
  }

  /** @param eventGokartFrame null is allowed input */
  public void setEventGokartFrame(double[] eventGokartFrame) {
    this.eventGokartFrame = eventGokartFrame;
  }

  /** @return can be null if event is further away than lookAheadDistance */
  public double[] getEventGokartFrame() {
    return eventGokartFrame;
  }
}
