// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.calib.SensorsConfig;
import ch.ethz.idsc.gokart.calib.vmu931.PlanarVmu931Type;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    SensorsConfig.GLOBAL.planarVmu931Type = PlanarVmu931Type.ROT90.name();
    GokartLogFile gokartLogFile = GokartLogFile._20191104T115120_825efe9d;
    File file = DatahakiLogFileLocator.file(gokartLogFile);
    // file = new File("/media/datahaki/data/gokart/visualize/20191104T105613_00", "log.lcm");
    // file = new File("/media/datahaki/media/ethz/gokart/topic/racing2r", "20180820T143852_1.lcm");
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("/media/datahaki/data/gokart/rmpcslip"), //
        gokartLogFile.getTitle());
  }
}
