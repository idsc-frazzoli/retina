// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.offline.gui.DynamicsConversion;
import ch.ethz.idsc.gokart.offline.gui.HtmlLogReport;

public enum DynamicsConversionBulk {
  ;
  public static void all(File folder) {
    for (File cut : Stream.of(folder.listFiles()).sorted().collect(Collectors.toList())) {
      System.out.println(cut);
      Optional<File> optional = DynamicsConversion.single(cut, StaticHelper.LOG_LCM, StaticHelper.DEST);
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
