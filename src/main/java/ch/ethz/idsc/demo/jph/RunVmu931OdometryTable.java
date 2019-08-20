// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.Vmu931OdometryTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum RunVmu931OdometryTable {
  ;
  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190314/20190314T154544_18");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder);
    File file = new File(folder, "post.lcm");
    Vmu931OdometryTable vmu931OdometryTable = new Vmu931OdometryTable(Quantity.of(0, SI.SECOND));
    vmu931OdometryTable.vmu931Odometry.resetPose(gokartLogInterface.pose());
    OfflineLogPlayer.process(file, vmu931OdometryTable);
    Export.of(HomeDirectory.file("vmu931_odometry.csv"), vmu931OdometryTable.getTable().map(CsvFormat.strict()));
  }
}
