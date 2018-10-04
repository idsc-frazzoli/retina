// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.filter.EventPolarityFilter;
import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets SlamCoreConfig parameters according to siliconEye */
/* package */ enum SEyeSCCLoader {
  ;
  public static SlamCoreConfig getSlamCoreConfig() {
    SlamCoreConfig slamCoreConfig = new SlamCoreConfig();
    /** SLAM algorithm configuration. Options are fields of {@link SlamAlgoConfig} */
    slamCoreConfig.slamAlgoConfig = SlamAlgoConfig.standardReactiveMode;
    /** when true, logs are recorded with timestamps provided by dvs event stream */
    slamCoreConfig.dvsTimeLogMode = false;
    /** when true, logs are recorded with periodic timestamps */
    slamCoreConfig.periodicLogMode = false;
    /** saves occurrence map. To be used to save ground truth map obtained with lidar pose */
    slamCoreConfig.saveSlamMap = false;
    /** which event polarities are processed */
    slamCoreConfig.eventPolarityFilter = EventPolarityFilter.BOTH;
    // particle filter parameters
    slamCoreConfig.alpha = RealScalar.of(0.5); // [-] for update of state estimate
    slamCoreConfig.numberOfParticles = RealScalar.of(20);
    /** only these particles are used for occurrence map update */
    slamCoreConfig.relevantParticles = RealScalar.of(4);
    /** average pose of particleRange with highest likelihood is set as pose estimate of the algorithm */
    slamCoreConfig.particleRange = RealScalar.of(3);
    /** events further away are neglected */
    slamCoreConfig.lookAheadDistance = Quantity.of(12, SI.METER);
    /** for reactive mapping modes */
    slamCoreConfig.lookBehindDistance = Quantity.of(-2, SI.METER);
    // for SiliconEye sensor which has very wide field of view
    slamCoreConfig.cropLowerPart = Quantity.of(2, SI.METER);
    slamCoreConfig.cropSides = Quantity.of(2, SI.METER);
    // update rates
    slamCoreConfig.localizationUpdateRate = Quantity.of(4, NonSI.MILLI_SECOND); // external pose update rate
    slamCoreConfig.resampleRate = Quantity.of(20, NonSI.MILLI_SECOND);
    slamCoreConfig.statePropagationRate = Quantity.of(1, NonSI.MILLI_SECOND);
    slamCoreConfig.reactiveUpdateRate = Quantity.of(0.1, SI.SECOND);
    slamCoreConfig.waypointUpdateRate = Quantity.of(0.01, SI.SECOND);
    slamCoreConfig.poseMapUpdateRate = Quantity.of(0.5, SI.SECOND);
    slamCoreConfig.logCollectionUpdateRate = Quantity.of(0.1, SI.SECOND);
    slamCoreConfig.purePursuitUpdateRate = Quantity.of(0.02, SI.SECOND);
    // particle initialization
    slamCoreConfig.linVelAvg = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
    slamCoreConfig.linVelStd = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
    slamCoreConfig.angVelStd = Quantity.of(0.1, SI.PER_SECOND); // [rad/s] for initial particle distribution
    // particle roughening
    slamCoreConfig.rougheningLinAccelStd = Quantity.of(5, SI.ACCELERATION);
    slamCoreConfig.rougheningAngAccelStd = Quantity.of(12, "rad*s^-2");
    // SLAM map parameters
    slamCoreConfig.cellDim = Quantity.of(0.05, SI.METER); // single cell dimension
    /** map dimensions {width[m], height[m]} */
    slamCoreConfig.mapDimensions = Tensors.of(Quantity.of(35, SI.METER), Quantity.of(35, SI.METER)).unmodifiable();
    // SlamMapMove
    slamCoreConfig.padding = Quantity.of(4, SI.METER);
    // SlamViewer
    slamCoreConfig.saveSlamFrame = false;
    slamCoreConfig.savingInterval = Quantity.of(21, SI.SECOND);
    slamCoreConfig.visualizationInterval = Quantity.of(0.1, SI.SECOND);
    slamCoreConfig.frameWidth = RealScalar.of(600); // [pixel]
    slamCoreConfig.kartSize = Quantity.of(1.5, SI.METER);
    slamCoreConfig.waypointRadius = Quantity.of(0.17, SI.METER);
    return slamCoreConfig;
  }
}
