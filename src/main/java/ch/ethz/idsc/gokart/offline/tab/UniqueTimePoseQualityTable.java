// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** TimePoseQualityTable only exports unique pose messages
 * 
 * @see GokartPoseTable */
public class UniqueTimePoseQualityTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private Tensor last = Tensors.empty();

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseEvent gokartPoseInterface = new GokartPoseEvent(byteBuffer);
      Tensor pose = gokartPoseInterface.getPose();
      if (!last.equals(pose)) {
        tableBuilder.appendRow( //
            time.map(Magnitude.SECOND), //
            GokartPoseHelper.toUnitless(pose), //
            gokartPoseInterface.getQuality());
        last = pose;
      }
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  /** @param lcmfile
   * @param dest
   * @throws IOException */
  public static void process(File lcmfile, File dest) throws IOException {
    UniqueTimePoseQualityTable timePoseQualityTable = new UniqueTimePoseQualityTable();
    OfflineLogPlayer.process(lcmfile, timePoseQualityTable);
    dest.mkdir();
    String name = lcmfile.getName();
    Export.of( //
        new File(dest, name.substring(0, name.length() - 4) + ".csv"), //
        timePoseQualityTable.getTable().map(CsvFormat.strict()));
  }
}
