// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

/** class to switch between siliconEye and davis DVS sensor. config files are
 * set accordingly */
public class SlamDvsConfig {
  /** current options are "sEye" and "davis" */
  public static String cameraType = "sEye";

  /** @return SlamPrcConfig set according to cameraType */
  public static SlamPrcConfig getSlamPrcConfig() {
    if (cameraType.equals("sEye")) {
      return SEyeSPCLoader.getSlamPrcConfig();
    } else //
    if (cameraType.equals("davis")) {
      return DavisSPCLoader.getSlamPrcConfig();
    } else
      throw new RuntimeException();
  }

  /** @return SlamCoreConfig set according to cameraType */
  public static SlamCoreConfig getSlamCoreConfig() {
    if (cameraType.equals("sEye")) {
      return SEyeSCCLoader.getSlamCoreConfig();
    } else //
    if (cameraType.equals("davis")) {
      return DavisSCCLoader.getSlamCoreConfig();
    } else
      throw new RuntimeException();
  }

  /** @return DvsConfig set according to cameraType */
  public static DvsConfig getDvsConfig() {
    if (cameraType.equals("sEye")) {
      return SEyeDCLoader.getSlamCoreConfig();
    } else //
    if (cameraType.equals("davis")) {
      return DavisDCLoader.getSlamCoreConfig();
    } else
      throw new RuntimeException();
  }
}
