// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.filter.EventPolarityFilter;
import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;

/** defines parameters for the event-based SLAM algorithm */
public class SlamCoreConfig {
  public DvsConfig dvsConfig;
  public EventPolarityFilter eventPolarityFilter;
  public SlamAlgoConfig slamAlgoConfig;
  public Boolean dvsTimeLogMode;
  public Boolean periodicLogMode;
  public Boolean saveSlamMap;
  public Boolean saveSlamFrame;
  public Scalar alpha;
  public Scalar numberOfParticles;
  public Scalar relevantParticles;
  public Scalar particleRange;
  public Scalar lookAheadDistance;
  public Scalar cropLowerPart;
  public Scalar cropSides;
  public Scalar lookBehindDistance;
  public Scalar localizationUpdateRate;
  public Scalar resampleRate;
  public Scalar statePropagationRate;
  public Scalar reactiveUpdateRate;
  public Scalar waypointUpdateRate;
  public Scalar poseMapUpdateRate;
  public Scalar logCollectionUpdateRate;
  public Scalar purePursuitUpdateRate;
  public Scalar linVelAvg;
  public Scalar linVelStd;
  public Scalar angVelStd;
  public Scalar rougheningLinAccelStd;
  public Scalar rougheningAngAccelStd;
  public Scalar cellDim;
  public Tensor mapDimensions;
  public Scalar padding;
  public Scalar savingInterval;
  public Scalar visualizationInterval;
  public Scalar frameWidth;
  public Scalar kartSize;
  public Scalar waypointRadius;

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
    return StaticHelper.loadFromCSV(SlamFileLocations.RECORDED_MAP.inFolder(dvsConfig.logFilename()));
  }

  public final double kartLength() {
    return Magnitude.ONE.toDouble(kartSize.divide(cellDim));
  }

  public final double waypointRadius() {
    return Magnitude.ONE.toDouble(waypointRadius.divide(cellDim));
  }
}
