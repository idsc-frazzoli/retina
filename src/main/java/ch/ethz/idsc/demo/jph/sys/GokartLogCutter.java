// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

enum GokartLogCutter {
  ;
  /** azure:
   * _20180827T150209_db899976
   * _20180827T170643_db899976
   * _20180827T175941_db899976
   * _20180830T101537_db899976
   * _20180830T111749_db899976 */
  public static void main(String[] args) throws IOException {
    GokartLogFile gokartLogFile = GokartLogFile._20180827T155655_db899976;
    File file = DatahakiLogFileLocator.file(gokartLogFile);
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
    new GokartLcmLogCutter( //
        gokartLogFileIndexer, //
        new File("/media/datahaki/media/ethz/gokart/topic/mapping"), //
        gokartLogFile.getTitle());
  }
}
