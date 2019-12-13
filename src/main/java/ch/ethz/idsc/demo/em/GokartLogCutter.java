// code by jph
package ch.ethz.idsc.demo.em;

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
    // GokartLogFile gokartLogFile = GokartLogFile._20190819T120821_c21b2aba;
    File file = new File("C:/Users/Enrico/Documents/MATLAB/ethz binary file", "20191212T114521_5deaa13c.lcm.00");
    // file = new File("/media/datahaki/media/ethz/gokart/topic/racing2r", "20180820T143852_1.lcm");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("C:/Users/Enrico/Documents/MATLAB/ethz binary file/cutsfile"), //
        "20191212_c");
  }
}
