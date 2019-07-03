// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Imu;
import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Type;
import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmClient;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16FromPolarCoordinates;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16ToPolarCoordinates;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class SensorsConfig {
  public static final SensorsConfig GLOBAL = AppResources.load(new SensorsConfig());
  /***************************************************/
  /** angular offset that rotates the sensor azimuth 0 onto the gokart positive x axis */
  public final Scalar vlp16_twist = RealScalar.of(-1.61);
  /** transformation from center of rear-axle to vlp16 in (x, y)-plane
   * the third, i.e. angle coordinate has to be zero. */
  public final Tensor vlp16_pose = Tensors.fromString("{0.09[m], 0.0[m], 0.0}");
  /** vlp16_incline is the rotation of tilt around the y-axis of the gokart
   * 
   * due to the small magnitude of vlp16_incline, the approximations hold
   * Cos[vlp16_incline] ~ 1
   * Sin[vlp16_incline] ~ 0
   * then, vlp16_inclinea can correct a height of a lidar point
   * by adding an offset that is proportional to
   * the lidar x-coordinate to correct the lidar point z-coordinate:
   * z_gokart = z_lidar - vlp16_incline * x_lidar
   * post 20190530: incline different */
  public Scalar vlp16_incline = RealScalar.of(0.022);
  /** height of vlp scan rays from ground
   * 1.112[m] from ground to platform top (until 2018-02-25, the value was 1.18[m])
   * 0.006[m] width of heat sink
   * 0.0378[m] == 37.8[mm] from heat sink to height of optical center
   * total = 1.112 + 0.006 + 0.0378 == 1.1558
   * 
   * @see SafetyConfig */
  public final Scalar vlp16Height = Quantity.of(1.1558, SI.METER);
  /** number of rotations per second */
  public final Scalar vlp16_rate = Quantity.of(20, SI.PER_SECOND);
  /** relative zero is value in the interval [0, 1] */
  public final Scalar vlp16_relativeZero = DoubleScalar.of(0.75);
  // ---
  public final Scalar davis_imu_rate = Quantity.of(1000, SI.PER_SECOND);
  public final Tensor davis_frustum = Tensors.fromString("{0[m], 7[m]}");
  /** 20181212: the value for the imu bias was established from
   * the first 60[s] of the logs from December 6. and 11. */
  public final Scalar davis_imuY_bias = Quantity.of(0.0142, SI.PER_SECOND);
  /** due to the inclined mounting of the davis camera,
   * the imuY measurement may have to be scaled.
   * until 20180507 the factor was 1 because the davis camera
   * was upside down at almost no inclination.
   * on 20180514 the jaer-core was retired in favor of jAER1.5
   * the camera is in upright position and therefore
   * the scaling was set to -1.0
   * post 20180930: fitting to previous log data motivated a change to -1.02 */
  private final Scalar davis_imuY_scale = RealScalar.of(-1.02);
  /** shift from center of VLP16 to DAVIS */
  public final Tensor vlp16_davis_t = Tensors.vectorDouble(0.2, 0, 0.5);
  public final Tensor vlp16_davis_w0 = Tensors.vectorDouble(1.57, 0.0, 0.0);
  public final Tensor vlp16_davis_w1 = Tensors.vectorDouble(0.0, 3.0, 0.0);
  public String planarVmu931Type = PlanarVmu931Type.ROT90.name();

  /***************************************************/
  public Vlp16LcmHandler vlp16LcmHandler() {
    double angle_offset = vlp16_twist.number().doubleValue();
    return new Vlp16LcmHandler(GokartLcmChannel.VLP16_CENTER, angle_offset);
  }

  public Vlp16LcmClient vlp16LcmClient(VelodyneDecoder velodyneDecoder) {
    return new Vlp16LcmClient(velodyneDecoder, GokartLcmChannel.VLP16_CENTER);
  }

  public LidarSpacialProvider vlp16SpacialProvider() {
    double angle_offset = vlp16_twist.number().doubleValue();
    return new Vlp16SpacialProvider(angle_offset);
  }

  /** @return 3x3 matrix transforming points in lidar frame to gokart frame */
  public Tensor vlp16Gokart() {
    return PoseHelper.toSE2Matrix(vlp16_pose);
  }

  public int imuSamplesPerLidarScan() {
    return Round.of(davis_imu_rate.divide(vlp16_rate)).number().intValue();
  }

  public TensorUnaryOperator vlp16ToPolarCoordinates() {
    return new Vlp16ToPolarCoordinates(vlp16_twist);
  }

  public TensorUnaryOperator vlp16FromPolarCoordinates() {
    return new Vlp16FromPolarCoordinates(vlp16_twist);
  }

  /***************************************************/
  /** @param davisImuFrame
   * @return rate of gokart around z-axis derived from imu measurements in "s^-1" */
  public Scalar davisGyroZ(DavisImuFrame davisImuFrame) {
    return davisImuFrame.gyroImageFrame().Get(1).subtract(davis_imuY_bias) // image - y axis
        .multiply(davis_imuY_scale);
  }

  /***************************************************/
  /** .
   * ante 20190408: the vmu931 was mounted on the gokart with xyz aligned with the gokart coordinate system
   * post 20190408: the vmu931 is mounted rotated around U axis with 180[deg] */
  public PlanarVmu931Imu getPlanarVmu931Imu() {
    return PlanarVmu931Type.valueOf(planarVmu931Type).planarVmu931Imu();
  }
}
