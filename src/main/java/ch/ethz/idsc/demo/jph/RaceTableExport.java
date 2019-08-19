// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.LinmotGetChannel;
import ch.ethz.idsc.gokart.offline.channel.SteerColumnChannel;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
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
    return tableBuilder.toTable();
  }

  public static void main(String[] args) throws IOException {
    Tensor points_xyr = ResourceData.of("/dubilab/analysis/track/20190701.csv");
    BSplineTrack bSplineTrack = BSplineTrack.of(points_xyr, true);
    // ---
    File folder = new File("/media/datahaki/data/gokart/0701hum");
    for (File file : folder.listFiles()) {
      OfflineTableSupplier offlineTableSupplier = new RaceTableExport();
      OfflineLogPlayer.process(new File(file, "log.lcm"), offlineTableSupplier);
      Tensor table = offlineTableSupplier.getTable();
      Tensor tensor = Tensor.of(table.stream() //
          .map(row -> row.extract(1, 3)) //
          .map(xy -> bSplineTrack.getNearestPathProgress(xy)));
      Tensor monoto = Tensors.empty();
      Scalar offset = RealScalar.of(0);
      // monoto.append(tensor.get(0));
      Scalar curr = RealScalar.of(-1000);
      for (int count = 1; count < tensor.length(); ++count) {
        Scalar prev = tensor.Get(count - 1);
        Scalar next = tensor.Get(count - 0);
        if (Scalars.lessThan(RealScalar.of(14), prev) && //
            Scalars.lessThan(next, RealScalar.of(1))) {
          offset = offset.add(RealScalar.of(15));
        }
        Scalar prog = next.add(offset);
        if (Scalars.lessThan(curr, prog)) {
          curr = prog;
          monoto.append(table.get(count).append(prog));
        }
      }
      // Tensor result = Transpose.of(Transpose.of(table).append(monoto));
      System.out.println(Dimensions.of(monoto));
      Export.of(HomeDirectory.Documents("racing", file.getName() + "_hum.csv"), monoto);
    }
  }
}
