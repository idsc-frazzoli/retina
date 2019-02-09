// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum DynamicsConversion {
  ;
  private static final File ROOT = new File("/media/datahaki/data/gokart/cuts");
  private static final File DEST = new File("/media/datahaki/data/gokart/dynamics");

  public static void main(String[] args) {
    List<SingleChannelInterface> singleChannelInterfaces = Arrays.asList( //
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
    // ---
    for (File folder : ROOT.listFiles())
      if (!folder.getName().startsWith("_"))
        for (File cut : folder.listFiles()) {
          System.out.println(cut);
          File dest = new File(DEST, cut.getName());
          dest.mkdir();
          File file = new File(cut, "post.lcm");
          if (file.isFile()) {
            List<OfflineTableSupplier> offlineTableSuppliers = //
                singleChannelInterfaces.stream().map(SingleChannelTable::of).collect(Collectors.toList());
            try {
              OfflineLogPlayer.process(file, offlineTableSuppliers);
              for (int index = 0; index < singleChannelInterfaces.size(); ++index)
                Export.of( //
                    new File(dest, singleChannelInterfaces.get(index).channel() + ".csv.gz"), //
                    offlineTableSuppliers.get(index).getTable().map(CsvFormat.strict()));
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }
        }
  }
}
