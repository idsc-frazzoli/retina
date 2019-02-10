// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartStatusChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotPutChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoGetChannel;
import ch.ethz.idsc.gokart.offline.channel.RimoPutChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.gokart.offline.channel.SteerGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerPutChannel;
import ch.ethz.idsc.gokart.offline.channel.Vmu931ImuChannel;
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
  private static final List<SingleChannelInterface> SINGLE_CHANNEL_INTERFACES = Arrays.asList( //
      GokartPoseChannel.INSTANCE, //
      GokartPosePostChannel.INSTANCE, //
      GokartStatusChannel.INSTANCE, //
      RimoPutChannel.INSTANCE, //
      RimoGetChannel.INSTANCE, //
      SteerPutChannel.INSTANCE, //
      SteerGetChannel.INSTANCE, //
      LinmotPutChannel.INSTANCE, //
      LinmotGetChannel.INSTANCE, //
      Vmu931ImuChannel.INSTANCE);
  private static final String SMOOTH = "gokart.pose.smooth";

  public static void process(File cut) {
    System.out.println(cut);
    File dest = new File(DEST, cut.getName());
    dest.mkdir();
    File file = new File(cut, "post.lcm");
    int INDEX_POSE = IntStream.range(0, SINGLE_CHANNEL_INTERFACES.size()) //
        .filter(index -> SINGLE_CHANNEL_INTERFACES.get(index).equals(GokartPosePostChannel.INSTANCE)) //
        .findFirst().getAsInt();
    if (file.isFile()) {
      List<OfflineTableSupplier> offlineTableSuppliers = //
          SINGLE_CHANNEL_INTERFACES.stream().map(SingleChannelTable::of).collect(Collectors.toList());
      try {
        OfflineLogPlayer.process(file, offlineTableSuppliers);
        for (int index = 0; index < SINGLE_CHANNEL_INTERFACES.size(); ++index)
          Export.of( //
              new File(dest, SINGLE_CHANNEL_INTERFACES.get(index).channel() + ".csv.gz"), //
              offlineTableSuppliers.get(index).getTable().map(CsvFormat.strict()));
        // ---
        {
          Tensor pose = offlineTableSuppliers.get(INDEX_POSE).getTable().copy();
          Tensor tensor = Tensor.of(pose.stream().map(row -> row.extract(1, 4)));
          Tensor smooth = GokartPoseSmoothing.INSTANCE.apply(tensor).map(Round._6);
          for (int index = 0; index < 3; ++index)
            pose.set(smooth.get(Tensor.ALL, index), Tensor.ALL, index + 1);
          Export.of( //
              new File(dest, SMOOTH + ".csv.gz"), //
              pose);
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    File single = new File("/media/datahaki/data/gokart/cuts/20190208/20190208T145312_22");
    process(single);
    // for (File folder : ROOT.listFiles())
    // if (!folder.getName().startsWith("_"))
    // for (File cut : folder.listFiles())
    // process(cut);
  }
}
