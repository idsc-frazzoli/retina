// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/* package */ enum DynamicsConversionBulk {
  ;
  public static void main(String[] args) {
    final File folder = new File(StaticHelper.CUTS, "20190321");
    for (File cut : Stream.of(folder.listFiles()).sorted().collect(Collectors.toList())) {
      System.out.println(cut);
      Optional<File> optional = DynamicsConversion.single(cut, StaticHelper.POST_LCM);
      if (optional.isPresent())
        try {
          HtmlLogReport.generate(optional.get());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }
}
