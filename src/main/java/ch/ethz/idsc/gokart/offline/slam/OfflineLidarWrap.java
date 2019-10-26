// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.u3.GokartLabjackFrame;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.tensor.Scalar;

/** class produces table with the following columns:
 * 
 * time [s]
 * rimo torque left [ARMS]
 * rimo torque right [ARMS]
 * rimo rate left [rad*s^-1]
 * rimo rate right [rad*s^-1]
 * tangent speed [m*s^-1]
 * rotational rate [rad*s^-1]
 * gyro rate around gokart z-axis [rad*s^-1]
 * steering column encoder [SCE]
 * brake position [m]
 * localization pose x [m]
 * localization pose y [m]
 * localization pose theta [rad]
 * localization pose quality */
public class OfflineLidarWrap implements OfflineLogListener {
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  public final ScatterImageInvoke scatterImageInvoke;

  public OfflineLidarWrap(ScatterImageInvoke scatterImageInvoke) {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304, 2);
    LidarSpacialProvider lidarSpacialProvider = LocalizationConfig.GLOBAL.planarEmulatorVlp16();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    this.scatterImageInvoke = scatterImageInvoke;
    lidarAngularFiringCollector.addListener(scatterImageInvoke);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR)) {
      // offlineLocalize.setTime(time);
      velodyneDecoder.lasers(byteBuffer);
    } else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      scatterImageInvoke.getEvent(gokartPoseEvent);
    } else //
    if (channel.equals(GokartLcmChannel.LABJACK_U3_ADC)) {
      GokartLabjackFrame gokartLabjackFrame = new GokartLabjackFrame(byteBuffer);
      scatterImageInvoke.manualControl(gokartLabjackFrame);
    }
  }
}
