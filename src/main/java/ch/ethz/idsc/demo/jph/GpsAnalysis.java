// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.VelodynePosChannel;
import ch.ethz.idsc.gokart.offline.tab.SingleChannelTable;
import ch.ethz.idsc.gokart.offline.tab.VelodynePosTable;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** export to determine characteristics and accuracy of gps sensor
 * 
 * https://github.com/idsc-frazzoli/retina/issues/147 */
/* package */ enum GpsAnalysis {
  ;
  public static void main(String[] args) throws IOException {
    String channel = VelodyneLcmChannels.pos(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
    // ---
    File file = DatahakiLogFileLocator.file(GokartLogFile._20190526T170036_7f7422b3);
    // ---
    VelodynePosTable velodynePosTable = new VelodynePosTable(channel);
    OfflineTableSupplier offlineTableSupplier = SingleChannelTable.of(VelodynePosChannel.INSTANCE);
    OfflineLogPlayer.process(file, velodynePosTable, offlineTableSupplier);
    try (FileOutputStream fileOutputStream = new FileOutputStream(HomeDirectory.file("20190526T170036.csv"))) {
      lines(velodynePosTable.list().stream(), fileOutputStream);
    }
    Export.of(HomeDirectory.file("20190526T170036.gps.csv"), offlineTableSupplier.getTable());
  }

  private static void lines(Stream<String> stream, OutputStream outputStream) {
    try (PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(outputStream))) {
      stream.sequential().forEach(printWriter::println);
    }
  }
}
