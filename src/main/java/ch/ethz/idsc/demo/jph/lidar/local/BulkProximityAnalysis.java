// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.Export;

enum BulkProximityAnalysis {
  ;
  static final File OUTPUT = new File("/media/datahaki/media/ethz/gokartexport/vlp16artifacts");

  static void process(File file, File out) throws IOException {
    ProximityAnalysis proximityAnalysis = new ProximityAnalysis();
    OfflineLogPlayer.process(file, proximityAnalysis);
    Export.of(out, proximityAnalysis.getTable());
  }

  static void mass(File folder) throws IOException {
    for (File file : folder.listFiles()) {
      String name = file.getName();
      if (name.endsWith(".lcm")) {
        String title = name.substring(0, name.length() - 4);
        File out = new File(OUTPUT, title + ".csv");
        if (!out.isFile()) {
          System.out.println(title);
          process(file, out);
        }
      }
    }
  }

  static void bulk(File folder) throws IOException {
    for (File file : folder.listFiles()) {
      String title = file.getName();
      File out = new File(OUTPUT, title + ".csv");
      if (!out.isFile()) {
        System.out.println(title);
        process(new File(file, "log.lcm"), out);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    {
      File[] folders = { //
          new File("/media/datahaki/media/ethz/gokart/topic/racing0w"), //
          new File("/media/datahaki/media/ethz/gokart/topic/racing2r"), //
          new File("/media/datahaki/media/ethz/gokart/topic/racing3az"), //
          new File("/media/datahaki/media/ethz/gokart/topic/racing4o"), //
      };
      // new File("/media/datahaki/media/ethz/gokart/topic/localization/20181206T122251_1", "log.lcm")
      for (File folder : folders)
        mass(folder);
    }
    // ---
    {
      bulk(new File("/media/datahaki/media/ethz/gokart/topic/localization"));
      bulk(new File("/media/datahaki/media/ethz/gokart/topic/odometry"));
    }
  }
}
