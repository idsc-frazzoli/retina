// code by mg
package ch.ethz.idsc.retina.app.slam.config;

/** class to switch between siliconEye and davis DVS sensor. config files are
 * set accordingly */
public enum SlamDvsConfig {
  ;
  // TODO JPH design no good
  public static EventCamera eventCamera = EventCamera.DAVIS;
}
