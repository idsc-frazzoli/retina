// code by jph
package ch.ethz.idsc.demo.ta;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Type;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    SensorsConfig.GLOBAL.planarVmu931Type = PlanarVmu931Type.ROT90.name();
    File file = new File("C:\\Users\\me\\Documents\\2019\\ETH\\Sem proj\\20191022\\20191022T135214_e9728d8b.lcm.00");
    // file = new File("/media/datahaki/media/ethz/gokart/topic/racing2r", "20180820T143852_1.lcm");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("C:\\Users\\me\\Documents\\2019\\ETH\\Sem proj\\cut"), //
        "20191022T135214_e9728d8b");
  }
}
