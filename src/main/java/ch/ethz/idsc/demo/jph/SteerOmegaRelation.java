// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.steer.GokartStatusEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartStatusEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.GokartStatusChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.CsvFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;

/** using:
 * calibrated steering
 * imu gryo z rotational rate
 * 
 * 20190507: map from x[SCE] to turning ratio is
 * 0.8284521034333863` x - 0.33633373640449604` x^3 */
/* package */ class SteerOmegaRelation implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  GokartStatusEvent gokartStatusEvent = GokartStatusEvents.UNKNOWN;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartStatusChannel.INSTANCE.channel())) {
      gokartStatusEvent = new GokartStatusEvent(byteBuffer);
    } else //
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      if (gokartStatusEvent.isSteerColumnCalibrated())
        tableBuilder.appendRow( //
            Magnitude.SECOND.apply(time), //
            gokartPoseEvent.asVector(), //
            gokartStatusEvent.asVector());
    }
  }

  @Override // from OfflineTableSupplier
  public Tensor getTable() {
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    File root = new File("/media/datahaki/data/gokart/cuts5/20190507m");
    File target = HomeDirectory.Documents("steeromega");
    target.mkdirs();
    for (File folder : root.listFiles()) {
      System.out.println(folder);
      SteerOmegaRelation steerOmegaRelation = new SteerOmegaRelation();
      OfflineLogPlayer.process(new File(folder, "log.lcm"), steerOmegaRelation);
      Tensor tensor = steerOmegaRelation.getTable();
      Export.of(new File(target, folder.getName() + ".csv"), tensor.map(CsvFormat.strict()));
    }
  }
}
