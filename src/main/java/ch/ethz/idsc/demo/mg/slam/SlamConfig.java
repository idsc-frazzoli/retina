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
  // SLAM algorithm parameters
  public final Boolean localizationMode = false; // in localization mode, a previously saved map is used
  public final Boolean lidarMappingMode = false; // pose provided by lidar instead of particle filter
  public final Boolean saveSlamMap = false;
  public final Scalar alpha = RealScalar.of(0.4); // [-] for update of state estimate
  public final Scalar numberOfParticles = RealScalar.of(30);
  public final Scalar relevantParticles = RealScalar.of(5); // only these particles are used for occurrence map update
  public final Scalar lookAheadDistance = RealScalar.of(7); // [m] events further away are neglected
  // update rates
  public final Scalar resampleRate = RealScalar.of(0.025); // [s]
  public final Scalar statePropagationRate = RealScalar.of(0.01); // [s]
  public final Scalar normalizationUpdateRate = RealScalar.of(0.05); // [s]
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
  public final Scalar savingInterval = RealScalar.of(250); // [ms]
  public final Scalar visualizationInterval = RealScalar.of(100); // [ms]
  public final Scalar kartSize = RealScalar.of(1.5); // [m]
}
