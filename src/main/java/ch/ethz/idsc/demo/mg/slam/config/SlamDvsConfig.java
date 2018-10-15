// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

/** class to switch between siliconEye and davis DVS sensor. config files are
 * set accordingly */
public class SlamDvsConfig {
  /** current options are "sEye" and "davis" */
  public static String cameraType = "davis";

  /** @return SlamPrcConfig set according to cameraType */
  public static SlamPrcConfig getSlamPrcConfig() {
    if (cameraType.equals("sEye")) {
      return new SEyeSlamPrcConfig();
    } else //
    if (cameraType.equals("davis")) {
      return new DavisSlamPrcConfig();
    } else
      throw new RuntimeException();
  }

  /** @return SlamCoreConfig set according to cameraType */
  public static SlamCoreConfig getSlamCoreConfig() {
    if (cameraType.equals("sEye")) {
      return new SEyeSlamCoreConfig();
    } else //
    if (cameraType.equals("davis")) {
      return new DavisSlamCoreConfig();
    } else
      throw new RuntimeException();
  }

  /** @return DvsConfig set according to cameraType */
  public static DvsConfig getDvsConfig() {
    if (cameraType.equals("sEye")) {
      return new SEyeDvsConfig();
    } else //
    if (cameraType.equals("davis")) {
      return new DavisDvsConfig();
    } else
      throw new RuntimeException();
  }
}
