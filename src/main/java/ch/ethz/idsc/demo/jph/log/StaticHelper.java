// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.offline.channel.DavisDvsChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartStatusChannel;
import ch.ethz.idsc.gokart.offline.channel.LabjackAdcChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotPutVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoPutChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.channel.SteerGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerPutChannel;
import ch.ethz.idsc.gokart.offline.channel.Vlp16RayChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuVehicleChannel;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;

/* package */ enum StaticHelper {
  ;
  static final String EXTENSION = ".csv.gz";
  static final List<SingleChannelInterface> SINGLE_CHANNEL_INTERFACES = Arrays.asList( //
      GokartPoseChannel.INSTANCE, //
      GokartPosePostChannel.INSTANCE, //
      GokartStatusChannel.INSTANCE, //
      RimoPutChannel.INSTANCE, //
      RimoGetChannel.INSTANCE, //
      SteerPutChannel.INSTANCE, //
      SteerGetChannel.INSTANCE, //
      LinmotPutVehicleChannel.INSTANCE, //
      LinmotGetVehicleChannel.INSTANCE, //
      DavisDvsChannel.INSTANCE, //
      Vlp16RayChannel.INSTANCE, //
      Vmu931ImuVehicleChannel.INSTANCE, //
      LabjackAdcChannel.INSTANCE //
  );
  static final String GOKART_POSE_SMOOTH = "gokart.pose.smooth";
  static final String LOG_START_TIME = "logStartTime_us.txt";
  // ---
  static final File CUTS = new File("/media/datahaki/data/gokart/cuts");
  static final File DEST = new File("/media/datahaki/data/gokart/dynamics");
  static final String LOG_LCM = "log.lcm";
  static final String POST_LCM = "post.lcm";
}
