// code by jph, set up for Toms laptop
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
    File file = new File("C:\\Users\\me\\Documents\\2019\\ETH\\Sem proj\\20191210\\20191210T143339_8c882510.lcm.00");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("C:\\Users\\me\\Documents\\2019\\ETH\\Sem proj\\cut"), //
        "cutMPC3");
  }
}
