// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** defines all parameters of the SLAM algorithm and optionally saves them to a .properties file */
public class SlamConfig {
  // general parameters
  public final DavisConfig davisConfig = new DavisConfig(); // main/resources/
  // SLAM algorithm configuration
  public final Boolean localizationMode = false; // in localization mode, a previously saved map is used
  public final Boolean lidarMappingMode = false; // pose provided by lidar instead of particle filter
  public final Boolean reactiveMappingMode = false; // in reactive mode, only the part of the map around the go kart is kept
  public final Boolean odometryStatePropagation = false; // state propagation using odometry instead of estimated velocities
  public final Boolean saveSlamMap = false; // saves the final map. to be used for saving ground truth maps
  // further parameters
  public final Scalar alpha = RealScalar.of(0.4); // [-] for update of state estimate
  public final Scalar numberOfParticles = RealScalar.of(20); // [-]
  public final Scalar relevantParticles = RealScalar.of(5); // only these particles are used for occurrence map update
  public final Scalar lookAheadDistance = RealScalar.of(13); // [m] events further away are neglected
  public final Scalar lookBehindDistance = RealScalar.of(-3); // [m] for reactive mapping mode
  // update rates
  public final Scalar resampleRate = RealScalar.of(0.05); // [s]
  public final Scalar statePropagationRate = RealScalar.of(0.005); // [s]
  public final Scalar reactiveUpdateRate = RealScalar.of(0.5); // [s]
  public final Scalar normalizationUpdateRate = RealScalar.of(0.05); // [s]
  public final Scalar wayPointUpdateRate = RealScalar.of(0.1); // [s]
  public final Scalar trajectoryUpdateRate = RealScalar.of(0.1); // [s]
  // particle initialization
  public final Scalar linVelAvg = RealScalar.of(3); // [m/s] for initial particle distribution
  public final Scalar linVelStd = RealScalar.of(1); // [m/s] for initial particle distribution
  public final Scalar angVelStd = RealScalar.of(0.1); // [rad/s] for initial particle distribution
  // particle roughening
  public final Scalar rougheningLinAccelStd = RealScalar.of(8); // [m/s²]
  public final Scalar rougheningAngAccelStd = RealScalar.of(10); // [rad/s²]
  // SLAM map parameters
  public final Scalar cellDim = RealScalar.of(0.025); // [m] single cell dimension
  public final Scalar dimX = RealScalar.of(35); // [m] x 'width' of map
  public final Scalar dimY = RealScalar.of(35); // [m] y 'height' of map
  public final Tensor corner = Tensors.vector(35, 35); // [m] coordinates of lower left point in map
  // SLAM visualization parameters
  public final Boolean saveSlamFrame = false;
  public final Scalar savingInterval = RealScalar.of(0.2); // [s]
  public final Scalar visualizationInterval = RealScalar.of(0.2); // [s]
  public final Scalar kartSize = RealScalar.of(1.5); // [m]
  public final Scalar wayPointRadius = RealScalar.of(10); // [pixel]
  // map processing parameters
  public final Scalar mapThreshold = RealScalar.of(0.3); // [-]
  // trajectory planning parameters
  public final Scalar initialDelay = RealScalar.of(1); // [s] initial delay before waypoints are extracted
  public final Scalar visibleBoxXMin = RealScalar.of(2); // [s] in go kart frame
  public final Scalar visibleBoxXMax = RealScalar.of(10); // [m] in go kart frame
  public final Scalar visibleBoxHalfWidth = RealScalar.of(5); // [m]
}
