// code by az
package ch.ethz.idsc.demo.gz;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.davis.DavisLcmLogUzhConvert;

/* package */ enum DavisLcmLogUzhConverterBatch {
  ;
  public static void process(File file, File destination) {
    DavisLcmLogUzhConvert.of(file, destination);
  }

  public static void main(String[] args) {
    File dir = new File("/home/gio/Downloads/logs/");
    File destination = new File("/home/gio/Downloads/logs/");
    for (File file : dir.listFiles()) {
      System.out.println(file);
      process(file, destination);
    }
  }
}
