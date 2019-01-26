// code by mh, jph
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.tab.DavisImuTable;
import ch.ethz.idsc.gokart.offline.tab.GokartPoseTable;
import ch.ethz.idsc.gokart.offline.tab.LinmotPassiveStatusTable;
import ch.ethz.idsc.gokart.offline.tab.PowerRimoAnalysis;
import ch.ethz.idsc.gokart.offline.tab.PowerSteerTable;
import ch.ethz.idsc.gokart.offline.tab.RimoOdometryTable;
import ch.ethz.idsc.gokart.offline.tab.RimoRateTable;
import ch.ethz.idsc.gokart.offline.tab.VelodyneLocalizationTable;
import ch.ethz.idsc.gokart.offline.tab.Vmu931ImuTable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

/** export of various content to determine accuracy of measurements.
 * export data for system identification */
public class ComprehensiveLogTableExport {
  private static final Scalar PERIOD = Quantity.of(0, SI.SECOND);
  private static final Scalar STEERINGPERIOD = Quantity.of(0.01, SI.SECOND);
  private static final Scalar POWERPERIOD = Quantity.of(0.01, SI.SECOND);
  private static final Scalar OFFSET = Quantity.of(0, SI.SECOND);
  // ---
  private final File outputFolder;

  public ComprehensiveLogTableExport(File outputFolder) {
    this.outputFolder = outputFolder;
    outputFolder.mkdirs();
    if (!outputFolder.isDirectory())
      throw new RuntimeException("directory does not exist");
  }

  /** @param file gokart log to be converted into csv tables
   * @throws IOException for instance, if given file does not exist */
  public void process(File file) throws IOException {
    DavisImuTable davisImuTable = new DavisImuTable(PERIOD);
    Vmu931ImuTable vmu931ImuTable = new Vmu931ImuTable(PERIOD);
    LinmotPassiveStatusTable linmotStatusTable = new LinmotPassiveStatusTable();
    PowerSteerTable powerSteerTable = new PowerSteerTable(STEERINGPERIOD);
    RimoOdometryTable rimoOdometryTable = new RimoOdometryTable();
    PowerRimoAnalysis powerRimoAnalysis = new PowerRimoAnalysis(POWERPERIOD);
    RimoRateTable rimoRateTable = new RimoRateTable(POWERPERIOD);
    // RimoSlipTable rimoSlipTable = new RimoSlipTable(PERIOD);
    // LocalizationTable localizationTable = new LocalizationTable(PERIOD, true);
    VelodyneLocalizationTable velodyneLocalizationTable = new VelodyneLocalizationTable(PERIOD);
    GokartPoseTable gokartPoseTable = new GokartPoseTable(PERIOD);
    //
    OfflineLogPlayer.process(file, //
        davisImuTable, //
        linmotStatusTable, powerSteerTable, //
        // rimoOdometryTable, //
        powerRimoAnalysis, //
        rimoRateTable, //
        vmu931ImuTable, //
        // rimoSlipTable);
        // localizationTable);
        // velodyneLocalizationTable);
        gokartPoseTable);
    //
    File folder = createTableFolder(file);
    // ---
    Export.of(new File(folder, "davisIMU.csv"), davisImuTable.getTable().map(CsvFormat.strict()));
    Export.of(new File(folder, "vmu931IMU.csv"), vmu931ImuTable.getTable().map(CsvFormat.strict()));
    Export.of(new File(folder, "powersteer.csv"), powerSteerTable.getTable().map(CsvFormat.strict()));
    // Export.of(new File(folder, "rimoodom.csv"), rimoOdometryTable.getTable().map(CsvFormat.strict()));
    Export.of(new File(folder, "powerrimo.csv"), powerRimoAnalysis.getTable().map(CsvFormat.strict()));
    Export.of(new File(folder, "rimorate.csv"), rimoRateTable.getTable().map(CsvFormat.strict()));
    // Export.of(UserHome.file("rimoslip.csv"), rimoSlipTable.getTable().map(CsvFormat.strict()));
    // Export.of(new File(folder, "localization.csv"), localizationTable.getTable().map(CsvFormat.strict()));
    // Export.of(new File(folder, "vlocalization.csv"), velodyneLocalizationTable.getTable().map(CsvFormat.strict()));
    Export.of(new File(folder, "gplocalization.csv"), gokartPoseTable.getTable().map(CsvFormat.strict()));
    Export.of(new File(folder, "linmot.csv"), linmotStatusTable.getTable().map(CsvFormat.strict()));
  }

  private File createTableFolder(File file) {
    String name = file.getName();
    int index = name.indexOf('_');
    if (0 < index)
      name = name.substring(0, index);
    File folder = new File(outputFolder, name);
    folder.mkdir();
    return folder;
  }
}
