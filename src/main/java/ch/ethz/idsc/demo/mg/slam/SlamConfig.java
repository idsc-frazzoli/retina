// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.DavisConfig;
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
public class SlamConfig {
  // general parameters
  public final DavisConfig davisConfig = new DavisConfig(); // main/resources/
  /** SLAM algorithm configuration. Options are fields of {@link SlamAlgoConfig}
   * access via member function below */
  private SlamAlgoConfig slamAlgoConfig = SlamAlgoConfig.standardReactiveMode;

  public SlamAlgoConfig slamAlgoConfig() {
    return slamAlgoConfig;
  }

  /** saves occurrence map. To be used to save ground truth map obtained with lidar pose */
  public final Boolean saveSlamMap = false;
  // further parameters
  public final Scalar alpha = RealScalar.of(0.4); // [-] for update of state estimate
  public final Scalar numberOfParticles = Quantity.of(30, SI.ONE);
  public final Scalar relevantParticles = RealScalar.of(5); // only these particles are used for occurrence map update
  /** average pose of particleRange with highest likelihood is set as pose estimate of the algorithm */
  public final Scalar particleRange = Quantity.of(3, SI.ONE);
  /** events further away are neglected */
  public final Scalar lookAheadDistance = Quantity.of(8, SI.METER);
  /** for reactive mapping mode */
  public final Scalar lookBehindDistance = Quantity.of(-1, SI.METER);
  // update rates
  public final Scalar localizationUpdateRate = Quantity.of(4, NonSI.MILLI_SECOND); // external pose update rate
  public final Scalar resampleRate = Quantity.of(0.05, SI.SECOND);
  public final Scalar statePropagationRate = Quantity.of(5, NonSI.MILLI_SECOND);
  public final Scalar reactiveUpdateRate = Quantity.of(0.5, SI.SECOND);
  public final Scalar waypointUpdateRate = Quantity.of(0.1, SI.SECOND);
  public final Scalar waypointSelectionUpdateRate = Quantity.of(0.1, SI.SECOND);
  public final Scalar poseMapUpdateRate = Quantity.of(0.5, SI.SECOND);
  // particle initialization
  public final Scalar linVelAvg = Quantity.of(2, SI.VELOCITY); // for initial particle distribution
  public final Scalar linVelStd = Quantity.of(1, SI.VELOCITY); // for initial particle distribution
  public final Scalar angVelStd = Quantity.of(0.1, SI.PER_SECOND); // [rad/s] for initial particle distribution
  // particle roughening
  public final Scalar rougheningLinAccelStd = Quantity.of(5, SI.ACCELERATION);
  public final Scalar rougheningAngAccelStd = Quantity.of(12, "rad*s^-2");
  // SLAM map parameters
  public final Scalar cellDim = Quantity.of(0.05, SI.METER); // single cell dimension
  /** [m] x 'width' of map */
  public final Scalar dimX = Quantity.of(35, SI.METER);
  /** [m] y 'height' of map */
  public final Scalar dimY = Quantity.of(35, SI.METER);

  public final int mapWidth() {
    return Magnitude.ONE.toInt(dimX.divide(cellDim));
  }

  public final int mapHeight() {
    return Magnitude.ONE.toInt(dimY.divide(cellDim));
  }

  /** @return [m] coordinates of lower left point in map */
  public final Tensor corner = Tensors.of( //
      Quantity.of(30, SI.METER), Quantity.of(30, SI.METER)).map(UnitSystem.SI());

  /** @return [m] coordinates of upper right point in map */
  public Tensor cornerHigh() {
    return corner.add(Tensors.of(dimX, dimY).map(UnitSystem.SI()));
  }

  /** @return mapArray containing ground truth occurrence map */
  public double[] getMapArray() {
    return PrimitivesIO.loadFromCSV(SlamFileLocations.recordedMaps(davisConfig.logFilename()));
  }

  // SlamMapProcessing
  public final Scalar mapThreshold = RealScalar.of(0.3); // valid range [0,1]
  public final Scalar initialDelay = Quantity.of(0.5, SI.SECOND); // initial delay before way points are extracted
  public final Scalar visibleBoxHalfWidth = Quantity.of(2, SI.METER); // in go kart frame
  public final Scalar visibleBoxXMin = Quantity.of(0, SI.METER); // in go kart frame
  public final Scalar visibleBoxXMax = Quantity.of(6, SI.METER); // in go kart frame
  // SlamPoseReset
  public final Scalar resetPoseX = RealScalar.of(50); // [m]
  public final Scalar resetPoseY = RealScalar.of(50); // [m]
  public final Scalar padding = Quantity.of(5, SI.METER);
  // SlamViewer
  public final Boolean saveSlamFrame = false;
  public final Scalar frameWidth = RealScalar.of(600); // [pixel]
  public final Scalar savingInterval = Quantity.of(0.3, SI.SECOND);
  public final Scalar visualizationInterval = Quantity.of(0.1, SI.SECOND);
  public final Scalar kartSize = Quantity.of(1.5, SI.METER);
  public final Scalar waypointRadius = Quantity.of(0.25, SI.METER);

  public final int kartLength() {
    return Magnitude.ONE.toInt(kartSize.divide(cellDim));
  }

  public final int waypointRadius() {
    return Magnitude.ONE.toInt(waypointRadius.divide(cellDim));
  }
}
