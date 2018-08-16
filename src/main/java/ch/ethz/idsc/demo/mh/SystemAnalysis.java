// code by jph
package ch.ethz.idsc.demo.mh;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Spring;

import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.gokart.offline.tab.LinmotStatusTable;
import ch.ethz.idsc.gokart.offline.tab.LocalizationTable;
import ch.ethz.idsc.gokart.offline.tab.PowerRimoAnalysis;
import ch.ethz.idsc.gokart.offline.tab.PowerSteerTable;
import ch.ethz.idsc.gokart.offline.tab.RimoOdometryTable;
import ch.ethz.idsc.gokart.offline.tab.RimoRateTable;
import ch.ethz.idsc.gokart.offline.tab.RimoSlipTable;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export of davis240c imu content to determine accuracy of measurements.
 * subsequently, the gyro readings are used to stabilize the lidar based
 * localization algorithm.
 * 
 * https://github.com/idsc-frazzoli/retina/files/1801712/20180131_davis_imu.pdf */
enum SystemAnalysis {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    //export data for system identification
    List<String> filenames = new LinkedList<>();
    
    filenames.add("20180611T095800_851c404d.lcm.00");
    
    Scalar scalar = Quantity.of(1, SI.SECOND);
    Scalar offset = Quantity.of(0, SI.ONE);
    
    DavisImuTable davisImuTable = new DavisImuTable(scalar);
    LinmotStatusTable linmotStatusTable = new LinmotStatusTable(offset);
    PowerSteerTable powerSteerTable = new PowerSteerTable(scalar);
    RimoOdometryTable rimoOdometryTable = new RimoOdometryTable();
    PowerRimoAnalysis powerRimoAnalysis = new PowerRimoAnalysis(scalar);
    RimoRateTable rimoRateTable = new RimoRateTable(scalar);
    RimoSlipTable rimoSlipTable = new RimoSlipTable(scalar);
    LocalizationTable localizationTable = new LocalizationTable(scalar, true);
    
    for (String filename: filenames) {
      OfflineLogPlayer.process(UserHome.file(filename), davisImuTable);
      //OfflineLogPlayer.process(UserHome.file(filename), linmotStatusTable);
      OfflineLogPlayer.process(UserHome.file(filename), powerSteerTable);
      OfflineLogPlayer.process(UserHome.file(filename), rimoOdometryTable);
      OfflineLogPlayer.process(UserHome.file(filename), powerRimoAnalysis);
      OfflineLogPlayer.process(UserHome.file(filename), rimoRateTable);
      //OfflineLogPlayer.process(UserHome.file(filename), rimoSlipTable);
      OfflineLogPlayer.process(UserHome.file(filename), localizationTable);
    }
    
    Export.of(UserHome.file("davisIMU.csv"), davisImuTable.getTable().map(CsvFormat.strict()));
    //Export.of(UserHome.file("linmot.csv"), linmotStatusTable.getTable().map(CsvFormat.strict()));
    Export.of(UserHome.file("powersteer.csv"), powerSteerTable.getTable().map(CsvFormat.strict()));
    Export.of(UserHome.file("rimoodom.csv"), rimoOdometryTable.getTable().map(CsvFormat.strict()));
    Export.of(UserHome.file("powerrimo.csv"), powerRimoAnalysis.getTable().map(CsvFormat.strict()));
    Export.of(UserHome.file("rimorate.csv"), rimoRateTable.getTable().map(CsvFormat.strict()));
    //Export.of(UserHome.file("rimoslip.csv"), rimoSlipTable.getTable().map(CsvFormat.strict()));
    Export.of(UserHome.file("localization.csv"), localizationTable.getTable().map(CsvFormat.strict()));

  
  }
}
