// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;

/* package */ enum DynamicsConversionBulk {
  ;
  private static final File ROOT = new File("/media/datahaki/data/gokart/cuts");

  public static void main(String[] args) {
    for (File folder : ROOT.listFiles())
      if (folder.getName().startsWith("_"))
        System.out.println("skip " + folder.getName());
      else
        for (File cut : folder.listFiles()) {
          System.out.println(cut);
          File directory = DynamicsConversion.single(cut);
          try {
            LogReport.generate(directory);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
  }
}
