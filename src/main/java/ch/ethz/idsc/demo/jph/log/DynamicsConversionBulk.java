// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.offline.gui.DynamicsConversion;
import ch.ethz.idsc.gokart.offline.gui.HtmlLogReport;

/* package */ enum DynamicsConversionBulk {
  ;
  public static void all(File folder) {
    for (File cut : Stream.of(folder.listFiles()).sorted().collect(Collectors.toList())) {
      System.out.println(cut);
      File folder2 = new File(StaticHelper.DEST, cut.getName().substring(0, 8)); // date e.g. 20190208
      folder2.mkdir();
      File target = new File(folder2, cut.getName());
      if (target.isDirectory())
        return;
      Optional<File> optional = DynamicsConversion.single(cut, StaticHelper.LOG_LCM, target);
      if (optional.isPresent())
        try {
          HtmlLogReport.generate(optional.get());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }

  public static void main(String[] args) {
    all(new File(StaticHelper.CUTS, "20190401"));
  }
}
