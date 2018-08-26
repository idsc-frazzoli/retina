// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.demo.mg.util.calibration.GokartToImageLookup;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;

/** defines all parameters of the SLAM algorithm and optionally saves them to a .properties file */
public class SlamConfig {
  // general parameters
  public final DavisConfig davisConfig = new DavisConfig(); // main/resources/
  /** SLAM algorithm configuration. Options are fields of {@link SlamAlgoConfig}
   * access via member function below */
  public String slamAlgoConfig = SlamAlgoConfig.odometryMode.name();

  public SlamAlgoConfig slamAlgoConfig() {
    return SlamAlgoConfig.valueOf(slamAlgoConfig);
  }

  /** saves occurrence map. To be used to save ground truth map obtained with lidar pose */
  public final Boolean saveSlamMap = false;
  // further parameters
  public final Scalar alpha = RealScalar.of(0.4); // [-] for update of state estimate
  public final Scalar numberOfParticles = RealScalar.of(20); // [-]
  public final Scalar relevantParticles = RealScalar.of(5); // only these particles are used for occurrence map update
  /** [m] events further away are neglected */
  public final Scalar lookAheadDistance = Quantity.of(8, SI.METER);
  /** [m] for reactive mapping mode */
  public final Scalar lookBehindDistance = Quantity.of(-3, SI.METER);
  // SlamLocalizationStep
  // update rates
  public final Scalar resampleRate = Quantity.of(0.05, SI.SECOND);
  public final Scalar statePropagationRate = Quantity.of(5, NonSI.MILLI_SECOND);
  public final Scalar reactiveUpdateRate = Quantity.of(0.5, SI.SECOND);
  public final Scalar normalizationUpdateRate = Quantity.of(0.05, SI.SECOND);
  public final Scalar wayPointUpdateRate = Quantity.of(0.1, SI.SECOND);
  public final Scalar trajectoryUpdateRate = Quantity.of(0.1, SI.SECOND);
  // particle initialization
  public final Scalar linVelAvg = Quantity.of(3, SI.VELOCITY); // [m/s] for initial particle distribution
  public final Scalar linVelStd = Quantity.of(1, SI.VELOCITY); // [m/s] for initial particle distribution
  public final Scalar angVelStd = Quantity.of(0.1, SI.PER_SECOND); // [rad/s] for initial particle distribution
  // particle roughening
  public final Scalar rougheningLinAccelStd = Quantity.of(8, SI.ACCELERATION); // [m/s²]
  public final Scalar rougheningAngAccelStd = Quantity.of(10, "rad*s^-2"); // [rad/s²]
  // SLAM map parameters
  public final Scalar cellDim = Quantity.of(0.025, SI.METER); // [m] single cell dimension
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

  // [m] coordinates of lower left point in map
  public final Tensor corner = Tensors.of( //
      Quantity.of(30, SI.METER), Quantity.of(30, SI.METER)).map(UnitSystem.SI());

  /** @return [m] coordinates of upper right point in map */
  public Tensor cornerHigh() {
    return corner.add(Tensors.of(dimX, dimY).map(UnitSystem.SI()));
  }

  // SlamViewer
  public final Boolean saveSlamFrame = false;
  public final Scalar frameWidth = RealScalar.of(600); // [pixel]
  public final Scalar savingInterval = Quantity.of(0.2, SI.SECOND);
  public final Scalar visualizationInterval = Quantity.of(0.2, SI.SECOND); // [s]
  public final Scalar kartSize = Quantity.of(1.5, SI.METER); // [m]

  public final int kartLength() {
    return Magnitude.ONE.toInt(kartSize.divide(cellDim));
  }

  public final Scalar wayPointRadius = RealScalar.of(10); // [pixel]
  // map processing parameters
  public final Scalar mapThreshold = RealScalar.of(0.3); // [-]
  // trajectory planning parameters
  public final Scalar initialDelay = Quantity.of(0.5, SI.SECOND); // [s] initial delay before waypoints are extracted
  public final Scalar visibleBoxXMin = Quantity.of(0, SI.METER); // [m] in go kart frame
  public final Scalar visibleBoxXMax = Quantity.of(10, SI.METER); // [m] in go kart frame

  /** @return new instance of {@link GokartToImageLookup} */
  // TODO MG function is not used in any relevant place, can we remove it?
  public GokartToImageLookup createGokartToImageLookup() {
    return GokartToImageLookup.fromMatrix(davisConfig.logFileLocations.calibration(), //
        davisConfig.unitConversion, cellDim, lookAheadDistance, Scalars.intValueExact(davisConfig.width));
  }
}
