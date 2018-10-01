// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.demo.mg.filter.EventPolarityFilter;
import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.retina.util.io.PrimitivesIO;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;

/** defines all parameters of the SLAM algorithm */
public class SlamCoreConfig {
  public static final SlamCoreConfig GLOBAL = new SlamCoreConfig();
  // general parameters
  public final DavisConfig davisConfig = new DavisConfig(); // main/resources/
  /** SLAM algorithm configuration. Options are fields of {@link SlamAlgoConfig} */
  public SlamAlgoConfig slamAlgoConfig = SlamAlgoConfig.odometryReactiveMode;
  /** when true, logs are recorded with timestamps provided by dvs event stream */
  public final Boolean dvsTimeLogMode = false;
  /** when true, logs are recorded with periodic timestamps */
  public final Boolean periodicLogMode = true;
  /** saves occurrence map. To be used to save ground truth map obtained with lidar pose */
  public final Boolean saveSlamMap = false;
  /** which event polarities are processed */
  public final EventPolarityFilter eventPolarityFilter = EventPolarityFilter.BOTH;
  // particle filter parameters
  public final Scalar alpha = RealScalar.of(0.5); // [-] for update of state estimate
  public Scalar numberOfParticles = RealScalar.of(20);
  /** only these particles are used for occurrence map update */
  public final Scalar relevantParticles = RealScalar.of(4);
  /** average pose of particleRange with highest likelihood is set as pose estimate of the algorithm */
  public final Scalar particleRange = RealScalar.of(3);
  /** events further away are neglected */
  public final Scalar lookAheadDistance = Quantity.of(5, SI.METER);
  /** for reactive mapping modes */
  public final Scalar lookBehindDistance = Quantity.of(-3, SI.METER);
  // update rates
  public final Scalar localizationUpdateRate = Quantity.of(4, NonSI.MILLI_SECOND); // external pose update rate
  public final Scalar resampleRate = Quantity.of(20, NonSI.MILLI_SECOND);
  public final Scalar statePropagationRate = Quantity.of(1, NonSI.MILLI_SECOND);
  public final Scalar reactiveUpdateRate = Quantity.of(0.1, SI.SECOND);
  public Scalar waypointUpdateRate = Quantity.of(0.01, SI.SECOND);
  public final Scalar poseMapUpdateRate = Quantity.of(0.5, SI.SECOND);
  public final Scalar logCollectionUpdateRate = Quantity.of(0.1, SI.SECOND);
  public Scalar purePursuitUpdateRate = Quantity.of(0.02, SI.SECOND);
  // particle initialization
  public final Scalar linVelAvg = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
  public final Scalar linVelStd = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
  public final Scalar angVelStd = Quantity.of(0.1, SI.PER_SECOND); // [rad/s] for initial particle distribution
  // particle roughening
  public final Scalar rougheningLinAccelStd = Quantity.of(5, SI.ACCELERATION);
  public final Scalar rougheningAngAccelStd = Quantity.of(12, "rad*s^-2");
  // SLAM map parameters
  public final Scalar cellDim = Quantity.of(0.05, SI.METER); // single cell dimension
  /** map dimensions {width[m], height[m]} */
  public final Tensor mapDimensions = Tensors.of(Quantity.of(35, SI.METER), Quantity.of(35, SI.METER)).unmodifiable();

  public final int mapWidth() {
    return Magnitude.ONE.toInt(mapDimensions.Get(0).divide(cellDim));
  }

  public final int mapHeight() {
    return Magnitude.ONE.toInt(mapDimensions.Get(1).divide(cellDim));
  }

  /** @return [m] coordinates of lower left point in map */
  public final Tensor corner = Tensors.of( //
      Quantity.of(30, SI.METER), Quantity.of(30, SI.METER)).map(UnitSystem.SI());

  /** @return [m] coordinates of upper right point in map */
  public Tensor cornerHigh() {
    return corner.add(mapDimensions.map(UnitSystem.SI()));
  }

  /** @return mapArray containing ground truth occurrence map */
  public double[] getMapArray() {
    return PrimitivesIO.loadFromCSV(SlamFileLocations.RECORDED_MAP.inFolder(davisConfig.logFilename()));
  }

  // SlamMapMove
  public final Scalar padding = Quantity.of(2, SI.METER);
  // SlamViewer
  public final Boolean saveSlamFrame = false;
  public final Scalar savingInterval = Quantity.of(5, SI.SECOND);
  public final Scalar visualizationInterval = Quantity.of(0.1, SI.SECOND);
  public final Scalar frameWidth = RealScalar.of(600); // [pixel]
  public final Scalar kartSize = Quantity.of(1.5, SI.METER);
  public final Scalar waypointRadius = Quantity.of(0.17, SI.METER);

  public final double kartLength() {
    return Magnitude.ONE.toDouble(kartSize.divide(cellDim));
  }

  public final double waypointRadius() {
    return Magnitude.ONE.toDouble(waypointRadius.divide(cellDim));
  }
}
