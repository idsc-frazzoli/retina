// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartStatusChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotPutVehicleChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoPutChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.channel.SteerGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerPutChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuVehicleChannel;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum DynamicsConversion {
  ;
  private static final File ROOT = new File("/media/datahaki/data/gokart/cuts");
  private static final File DEST = new File("/media/datahaki/data/gokart/dynamics");
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
      Vmu931ImuVehicleChannel.INSTANCE);
  static final String GOKART_POSE_SMOOTH = "gokart.pose.smooth";

  public static void process(File cut) {
    System.out.println(cut);
    File target = new File(DEST, cut.getName());
    target.mkdir();
    File file = new File(cut, "post.lcm");
    if (file.isFile()) {
      Map<SingleChannelInterface, OfflineTableSupplier> map = SINGLE_CHANNEL_INTERFACES.stream() //
          .collect(Collectors.toMap(Function.identity(), SingleChannelTable::of));
      try {
        OfflineLogPlayer.process(file, map.values());
        for (Entry<SingleChannelInterface, OfflineTableSupplier> entry : map.entrySet())
          Export.of( //
              new File(target, entry.getKey().exportName() + ".csv.gz"), //
              entry.getValue().getTable().map(CsvFormat.strict()));
        // ---
        {
          Tensor pose = map.get(GokartPosePostChannel.INSTANCE).getTable().copy();
          Tensor tensor = Tensor.of(pose.stream().map(row -> row.extract(1, 4)));
          Tensor smooth = GokartPoseSmoothing.INSTANCE.apply(tensor).map(Round._6);
          for (int index = 0; index < 3; ++index)
            pose.set(smooth.get(Tensor.ALL, index), Tensor.ALL, index + 1);
          Export.of( //
              new File(target, GOKART_POSE_SMOOTH + ".csv.gz"), //
              pose);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    } else
      System.err.println("missing: " + file);
  }

  public static void main(String[] args) {
    File single = new File("/media/datahaki/data/gokart/cuts/20190208/20190208T145312_04");
    process(single);
    // for (File folder : ROOT.listFiles())
    // if (!folder.getName().startsWith("_"))
    // for (File cut : folder.listFiles())
    // process(cut);
  }
}
