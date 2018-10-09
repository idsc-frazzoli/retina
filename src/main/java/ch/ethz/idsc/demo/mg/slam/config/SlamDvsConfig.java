// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

/** class to switch between siliconEye and davis DVS sensor. config files are
 * set accordingly */
public class SlamDvsConfig {
  public static EventCamera eventCamera = EventCamera.DAVIS;

  /** @return SlamCoreConfig set according to cameraType */
  public static SlamCoreConfig getSlamCoreConfig() {
    return eventCamera.slamCoreConfig;
  }

  /** @return DvsConfig set according to cameraType */
  public static DvsConfig getDvsConfig() {
    return eventCamera.slamCoreConfig.dvsConfig;
  }
}
