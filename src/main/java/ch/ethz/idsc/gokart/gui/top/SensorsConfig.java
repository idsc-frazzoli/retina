// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.io.Serializable;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.app.TiltedVelodynePlanarEmulator;
import ch.ethz.idsc.retina.dev.lidar.app.VelodynePlanarEmulator;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16SpacialLcmHandler;
import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class SensorsConfig implements Serializable {
  public static final SensorsConfig GLOBAL = AppResources.load(new SensorsConfig());
  /***************************************************/
  // TODO at some point also introduce units here
  /** urg04lx is the pose of the front lidar {px, py, angle} */
  public Tensor urg04lx = Tensors.vector(1.67, 0.0, 0.005);
  /** angular offset that rotates the sensor azimuth 0 onto the gokart positive x axis */
  public Scalar vlp16_twist = RealScalar.of(-1.61);
  /** transformation from center of rear-axle to vlp16 in (x,y)-plane
   * the third, i.e. angle coordinate has to be zero. */
  public Tensor vlp16 = Tensors.vector(0.09, 0.0, 0.0);
  /** vlp16_incline is the rotation of tilt around the y-axis of the gokart
   * 
   * due to the small magnitude of vlp16_incline, the approximations hold
   * Cos[vlp16_incline] == 1
   * Sin[vlp16_incline] == 0
   * then, vlp16_inclinea can correct a height of a lidar point
   * by adding an offset that is proportional to
   * the lidar x-coordinate to correct the lidar point z-coordinate:
   * z_gokart = z_lidar - vlp16_incline * x_lidar */
  public Scalar vlp16_incline = RealScalar.of(0.04);
  /** height of vlp scan from ground
   * 1.10[m] from ground to platform top (until 2018-02-25, the value was 1.18[m])
   * 0.006[m] TODO DUBENDORF width of heat sink
   * 0.0378[m] == 37.8[mm] from heat sink to height of optical center
   * total = 1.10 + 0.006 + 0.0378 == 1.1438 */
  // TODO vlp16Height is not yet used, instead see SafetyConfig
  public Scalar vlp16Height = Quantity.of(1.1438, SI.METER);
  /** shift from center of VLP16 to DAVIS */
  public Tensor vlp16_davis_t = Tensors.vectorDouble(0.2, 0, 0.5);
  public Tensor vlp16_davis_w0 = Tensors.vectorDouble(1.57, 0.0, 0.0);
  public Tensor vlp16_davis_w1 = Tensors.vectorDouble(0.0, 3.0, 0.0);

  /***************************************************/
  public Vlp16LcmHandler vlp16LcmHandler() {
    double angle_offset = vlp16_twist.number().doubleValue();
    return new Vlp16LcmHandler(GokartLcmChannel.VLP16_CENTER, angle_offset);
  }

  /** the VLP-16 on the is tilted by 0.04[rad] round the y-axis.
   * 
   * @return lidar spacial provider that approximates measurements
   * at the best approximation of zero inclination == horizon level */
  public LidarSpacialProvider horizontalEmulatorVlp16() {
    double angle_offset = vlp16_twist.number().doubleValue();
    // double tiltY = vlp16_incline.number().doubleValue();
    // return new TiltedVelodynePlanarEmulator(angle_offset, tiltY, 0.0);
    // TODO optimize TiltedVelodynePlanarEmulator and test offline
    // return TiltedVelodynePlanarEmulator.vlp16_p01deg(angle_offset);
    return VelodynePlanarEmulator.vlp16_p01deg(angle_offset);
  }

  @Deprecated
  public LidarSpacialProvider planarEmulatorVlp16_p01deg() {
    double angle_offset = vlp16_twist.number().doubleValue();
    // TODO optimize TiltedVelodynePlanarEmulator and test offline
    // return TiltedVelodynePlanarEmulator.vlp16_p01deg(angle_offset);
    return VelodynePlanarEmulator.vlp16_p01deg(angle_offset);
  }

  public Vlp16SpacialLcmHandler vlp16SpacialLcmHandler() {
    double angle_offset = vlp16_twist.number().doubleValue();
    return new Vlp16SpacialLcmHandler(GokartLcmChannel.VLP16_CENTER, angle_offset);
  }

  /** @return 3x3 matrix transforming points in lidar frame to gokart frame */
  public Tensor vlp16Gokart() {
    return Se2Utils.toSE2Matrix(vlp16).unmodifiable();
  }
}
