// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.offline.channel.DavisDvsChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.LabjackAdcChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotPutVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoPutChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.channel.SteerColumnChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerPutChannel;
import ch.ethz.idsc.gokart.offline.channel.Vlp16RayChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu932ImuChannel;

/* package */ enum StaticHelper {
  ;
  static final String EXTENSION = ".csv.gz";
  static final List<SingleChannelInterface> SINGLE_CHANNEL_INTERFACES = Arrays.asList( //
      GokartPoseChannel.INSTANCE, //
      SteerColumnChannel.INSTANCE, //
      RimoPutChannel.INSTANCE, //
      RimoGetChannel.INSTANCE, //
      SteerPutChannel.INSTANCE, //
      SteerGetChannel.INSTANCE, //
      LinmotPutVehicleChannel.INSTANCE, //
      LinmotGetVehicleChannel.INSTANCE, //
      DavisDvsChannel.INSTANCE, //
      Vlp16RayChannel.INSTANCE, //
      Vmu931ImuChannel.INSTANCE, //
      Vmu932ImuChannel.INSTANCE, //
      Vmu931ImuVehicleChannel.INSTANCE, //
      LabjackAdcChannel.INSTANCE //
  );
  static final String LOG_START_TIME = "logStartTime_us.txt";
}
