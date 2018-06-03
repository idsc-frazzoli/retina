// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.io.Serializable;

import ch.ethz.idsc.gokart.core.slam.LidarGyroLocalization;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.app.TiltedVelodynePlanarEmulator;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/**  */
public class LocalizationConfig implements Serializable {
  public static final LocalizationConfig GLOBAL = AppResources.load(new LocalizationConfig());
  /***************************************************/
  /** inclination of rays to create cross section
   * a positive value means upwards */
  public Scalar horizon = Quantity.of(1, NonSI.DEGREE_ANGLE);
  /** minimum number of lidar points below which a matching of lidar with
   * static geometry will not be executed and localization will not update */
  public Scalar min_points = RealScalar.of(250);
  public Scalar threshold = RealScalar.of(33.0);
  public Scalar resampleDs = RealScalar.of(0.4);

  /***************************************************/
  /** the VLP-16 is tilted by 0.04[rad] around the y-axis.
   * 
   * @return lidar spacial provider that approximates measurements
   * at the best approximation of given horizon level */
  public LidarSpacialProvider planarEmulatorVlp16() {
    SensorsConfig sensorsConfig = SensorsConfig.GLOBAL;
    double angle_offset = sensorsConfig.vlp16_twist.number().doubleValue();
    double tiltY = sensorsConfig.vlp16_incline.number().doubleValue();
    double emulation_deg = Magnitude.DEGREE_ANGLE.apply(horizon).number().doubleValue();
    return new TiltedVelodynePlanarEmulator(1, angle_offset, tiltY, emulation_deg);
  }

  public ParametricResample getUniformResample() {
    return new ParametricResample(threshold, resampleDs);
  }

  /***************************************************/
  /** @return predefined map with static geometry for lidar based localization */
  public static PredefinedMap getPredefinedMap() {
    return PredefinedMap.DUBENDORF_HANGAR_20180506;
  }

  /** @return new instance of LidarGyroLocalization method */
  public static LidarGyroLocalization getLidarGyroLocalization() {
    return new LidarGyroLocalization(getPredefinedMap());
  }

  public static PredefinedMap getPredefinedMapObstacles() {
    return PredefinedMap.DUBENDORF_HANGAR_20180423OBSTACLES;
  }
}
