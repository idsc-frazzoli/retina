// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.util.LinkedList;
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
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.tensor.io.Export;

enum DynamicsConversion {
  ;
  private static final File ROOT = new File("/media/datahaki/data/gokart/cuts");
  private static final File DEST = new File("/media/datahaki/data/gokart/dynamics");

  public static void main(String[] args) {
    List<SingleChannelInterface> singleChannelInterfaces = new LinkedList<>();
    singleChannelInterfaces.add(GokartPoseChannel.INSTANCE);
    singleChannelInterfaces.add(GokartStatusChannel.INSTANCE);
    singleChannelInterfaces.add(RimoPutChannel.INSTANCE);
    singleChannelInterfaces.add(RimoGetChannel.INSTANCE);
    singleChannelInterfaces.add(SteerPutChannel.INSTANCE);
    singleChannelInterfaces.add(SteerGetChannel.INSTANCE);
    singleChannelInterfaces.add(LinmotPutChannel.INSTANCE);
    singleChannelInterfaces.add(LinmotGetChannel.INSTANCE);
    // ---
    for (File folder : ROOT.listFiles()) {
      for (File cut : folder.listFiles()) {
        System.out.println(cut);
        File dest = new File(DEST, cut.getName());
        dest.mkdir();
        File file = new File(cut, "log.lcm");
        List<OfflineTableSupplier> offlineTableSuppliers = //
            singleChannelInterfaces.stream().map(SingleChannelTable::of).collect(Collectors.toList());
        try {
          OfflineLogPlayer.process(file, offlineTableSuppliers);
          for (int index = 0; index < singleChannelInterfaces.size(); ++index)
            Export.of( //
                new File(dest, singleChannelInterfaces.get(index).channel() + ".csv.gz"), //
                offlineTableSuppliers.get(index).getTable());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    }
  }
}
