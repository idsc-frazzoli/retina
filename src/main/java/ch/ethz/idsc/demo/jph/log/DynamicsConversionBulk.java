// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

/* package */ enum DynamicsConversionBulk {
  ;
  public static void main(String[] args) {
    for (File folder : StaticHelper.CUTS.listFiles())
      if (folder.getName().startsWith("_"))
        System.out.println("skip " + folder.getName());
      else
        for (File cut : folder.listFiles()) {
          System.out.println(cut);
          File directory = DynamicsConversion.single(cut);
          try {
            HtmlLogReport.generate(directory);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
  }
}
