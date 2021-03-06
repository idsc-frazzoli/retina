// code by mh, jph
package ch.ethz.idsc.demo.mh;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;

/** export of various content to determine accuracy of measurements.
 * export data for system identification */
/* package */ class ComprehensiveLogTableExportLite {
  // private static final Scalar STEERINGPERIOD = Quantity.of(0.01, SI.SECOND);
  // private static final Scalar POWERPERIOD = Quantity.of(0.01, SI.SECOND);
  // private static final Scalar OFFSET = Quantity.of(0, SI.SECOND);
  // ---
  private final File outputFolder;

  public ComprehensiveLogTableExportLite(File outputFolder) {
    this.outputFolder = outputFolder;
    outputFolder.mkdirs();
    if (!outputFolder.isDirectory())
      throw new RuntimeException("directory does not exist");
  }

  /** @param file gokart log to be converted into csv tables
   * @throws IOException for instance, if given file does not exist */
  public void process(File file) throws IOException {
    // OfflineTableSupplier davisImuTable = SingleChannelTable.of(DavisImuChannel.INSTANCE);
    // OfflineTableSupplier vmu931ImuTable = SingleChannelTable.of(Vmu931ImuChannel.INSTANCE);
    // LinmotPassiveStatusTable linmotStatusTable = new LinmotPassiveStatusTable();
    // PowerSteerTable powerSteerTable = new PowerSteerTable(STEERINGPERIOD);
    // RimoOdometryTable rimoOdometryTable = new RimoOdometryTable();
    // PowerRimoAnalysis powerRimoAnalysis = new PowerRimoAnalysis(POWERPERIOD);
    // RimoRateTable rimoRateTable = new RimoRateTable(POWERPERIOD);
    // RimoSlipTable rimoSlipTable = new RimoSlipTable(PERIOD);
    // LocalizationTable localizationTable = new LocalizationTable(PERIOD, true);
    // OfflineTableSupplier velodyneLocalizationTable = SingleChannelTable.of(VelodynePosChannel.INSTANCE);
    // OfflineTableSupplier gokartPoseTable = SingleChannelTable.of(GokartPoseChannel.INSTANCE);
    BasicSysIDTable basicSysIDTable = new BasicSysIDTable();
    //
    OfflineLogPlayer.process(file, //
        basicSysIDTable);
    //
    File folder = createTableFolder(file);
    // ---
    Export.of(new File(folder, "sysID.csv"), basicSysIDTable.getTable().map(CsvFormat.strict()));
    // Export.of(new File(folder, "sysID.csv"), basicSysIDTable.getTable());
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
