// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.util.Arrays;
import java.util.List;

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
      Vmu931ImuVehicleChannel.INSTANCE, //
      LabjackAdcChannel.INSTANCE //
  );
  static final String GOKART_POSE_SMOOTH = "gokart.pose.smooth";
  static final String LOG_START_TIME = "logStartTime_us.txt";
}
