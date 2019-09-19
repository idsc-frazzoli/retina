// code by jph
package ch.ethz.idsc.demo.jph.race;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerColumnChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class RaceTableExport implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private Scalar scec = Quantity.of(0, SteerPutEvent.UNIT_ENCODER);
  private Scalar brakePos = Quantity.of(0, SI.METER);

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartPoseChannel.INSTANCE.channel())) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      tableBuilder.appendRow( //
          Magnitude.SECOND.apply(time).map(Round._6), //
          gokartPoseEvent.asVector(), //
          SteerPutEvent.ENCODER.apply(scec).map(Round._5), //
          Magnitude.METER.apply(brakePos).map(Round._5));
    } else //
    if (channel.equals(LinmotGetChannel.INSTANCE.channel())) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      brakePos = linmotGetEvent.getActualPosition();
    } else //
    if (channel.equals(SteerColumnChannel.INSTANCE.channel())) {
      SteerColumnEvent steerColumnEvent = new SteerColumnEvent(byteBuffer);
      scec = steerColumnEvent.getSteerColumnEncoderCentered();
    }
  }

  @Override
  public Tensor getTable() {
    return tableBuilder.getTable();
  }
}
