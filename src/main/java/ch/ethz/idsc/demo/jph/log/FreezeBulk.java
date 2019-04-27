// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.offline.gui.ChannelCsvExport;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmMap;
import ch.ethz.idsc.gokart.offline.gui.HtmlLogReport;

/* package */ enum FreezeBulk {
  ;
  public static void main(String[] args) {
    final File folder = new File("/media/datahaki/data/gokart/freeze");
    for (File cut : Stream.of(folder.listFiles()).sorted().collect(Collectors.toList())) {
      System.out.println(cut);
      File file = new File(cut, StaticHelper.LOG_LCM);
      try {
        GokartLcmMap gokartLcmMap = new GokartLcmMap(file);
        ChannelCsvExport.of(gokartLcmMap, StaticHelper.DEST);
        new HtmlLogReport(gokartLcmMap, cut.getName(), StaticHelper.DEST);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }
}
