// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.filter.EventPolarityFilter;
import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets SlamCoreConfig parameters according to davis */
public class DavisSlamCoreConfig extends SlamCoreConfig {
  public static final DavisSlamCoreConfig GLOBAL = new DavisSlamCoreConfig();

  public DavisSlamCoreConfig() {
    /** SLAM algorithm configuration. Options are fields of {@link SlamAlgoConfig} */
    slamAlgoConfig = SlamAlgoConfig.standardReactiveMode;
    /** when true, logs are recorded with timestamps provided by dvs event stream */
    dvsTimeLogMode = true;
    /** when true, logs are recorded with periodic timestamps */
    periodicLogMode = false;
    /** saves occurrence map. To be used to save ground truth map obtained with lidar pose */
    saveSlamMap = false;
    /** which event polarities are processed */
    eventPolarityFilter = EventPolarityFilter.BOTH;
    // particle filter parameters
    alpha = RealScalar.of(0.5); // [-] for update of state estimate
    numberOfParticles = RealScalar.of(20);
    /** only these particles are used for occurrence map update */
    relevantParticles = RealScalar.of(4);
    /** average pose of particleRange with highest likelihood is set as pose estimate of the algorithm */
    particleRange = RealScalar.of(3);
    /** events further away are neglected */
    lookAheadDistance = Quantity.of(5, SI.METER);
    /** for reactive mapping modes */
    lookBehindDistance = Quantity.of(-3, SI.METER);
    // values below are not required for davis TODO are set to zero to avoid errors
    cropLowerPart = Quantity.of(0, SI.METER);
    cropSides = Quantity.of(0, SI.METER);
    // update rates
    localizationUpdateRate = Quantity.of(4, NonSI.MILLI_SECOND); // external pose update rate
    resampleRate = Quantity.of(20, NonSI.MILLI_SECOND);
    statePropagationRate = Quantity.of(1, NonSI.MILLI_SECOND);
    reactiveUpdateRate = Quantity.of(0.1, SI.SECOND);
    waypointUpdateRate = Quantity.of(0.01, SI.SECOND);
    poseMapUpdateRate = Quantity.of(0.5, SI.SECOND);
    logCollectionUpdateRate = Quantity.of(0.1, SI.SECOND);
    purePursuitUpdateRate = Quantity.of(0.02, SI.SECOND);
    // particle initialization
    linVelAvg = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
    linVelStd = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
    angVelStd = Quantity.of(0.1, SI.PER_SECOND); // [rad/s] for initial particle distribution
    // particle roughening
    rougheningLinAccelStd = Quantity.of(5, SI.ACCELERATION);
    rougheningAngAccelStd = Quantity.of(12, "rad*s^-2");
    // SLAM map parameters
    cellDim = Quantity.of(0.05, SI.METER); // single cell dimension
    /** map dimensions {width[m], height[m]} */
    mapDimensions = Tensors.of(Quantity.of(35, SI.METER), Quantity.of(35, SI.METER)).unmodifiable();
    // SlamMapMove
    padding = Quantity.of(4, SI.METER);
    // SlamViewer
    saveSlamFrame = false;
    savingInterval = Quantity.of(54, SI.SECOND);
    visualizationInterval = Quantity.of(0.1, SI.SECOND);
    frameWidth = RealScalar.of(600); // [pixel]
    kartSize = Quantity.of(1.5, SI.METER);
    waypointRadius = Quantity.of(0.17, SI.METER);
    dvsConfig = new DavisDvsConfig();
  }
}
