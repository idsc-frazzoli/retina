// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.io.Serializable;

import ch.ethz.idsc.gokart.core.slam.LidarGyroLocalization;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.ParametricResample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/**  */
public class LocalizationConfig implements Serializable {
  public static final LocalizationConfig GLOBAL = AppResources.load(new LocalizationConfig());
  /***************************************************/
  /** minimum number of lidar points below which a matching of lidar with
   * static geometry will not be executed and localization will not update */
  public Scalar min_points = RealScalar.of(250);
  public Scalar threshold = RealScalar.of(33.0);
  public Scalar resampleDs = RealScalar.of(0.4);

  /***************************************************/
  public ParametricResample getUniformResample() {
    return new ParametricResample(threshold, resampleDs);
  }

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
