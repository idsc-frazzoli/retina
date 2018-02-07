// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.sca.Round;

class LinmotStatusAnalysis implements OfflineTableSupplier {
  // private boolean status = false;
  final TensorBuilder tensorBuilder = new TensorBuilder();
  boolean isFused = false;
  Integer failure_index = null;

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      final boolean status = linmotGetEvent.isOperational();
      if (!isFused) {
        isFused |= status;
        if (isFused)
          System.out.println("calibrated at " + time);
      }
      // ---
      if (isFused) {
        if (!status && Objects.isNull(failure_index)) {
          System.out.println("failure at " + time);
          failure_index = tensorBuilder.size();
        }
        tensorBuilder.flatten( //
            time.map(Magnitude.SECOND).map(Round._6), //
            StringScalar.of("GET"), //
            RealScalar.of(linmotGetEvent.status_word), //
            RealScalar.of(linmotGetEvent.state_variable), //
            RealScalar.of(linmotGetEvent.actual_position), //
            RealScalar.of(linmotGetEvent.demand_position), //
            linmotGetEvent.getWindingTemperature1().map(Magnitude.DEGREE_CELSIUS).map(Round._1), //
            linmotGetEvent.getWindingTemperature2().map(Magnitude.DEGREE_CELSIUS).map(Round._1) //
        );
      }
    }
    if (channel.equals(LinmotLcmServer.CHANNEL_PUT) && isFused) {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent(byteBuffer);
      tensorBuilder.flatten( //
          time.map(Magnitude.SECOND).map(Round._6), //
          StringScalar.of("PUT"), //
          RealScalar.of(linmotPutEvent.control_word), //
          RealScalar.of(linmotPutEvent.motion_cmd_hdr), //
          RealScalar.of(linmotPutEvent.target_position), //
          RealScalar.of(linmotPutEvent.max_velocity), //
          RealScalar.of(linmotPutEvent.acceleration), //
          RealScalar.of(linmotPutEvent.deceleration) //
      );
    }
  }

  @Override
  public Tensor getTable() {
    if (!isFused)
      throw new RuntimeException("not fused");
    return tensorBuilder.getTensor().extract(failure_index - 20, failure_index + 20);
  }

  public static void main(String[] args) throws IOException {
    Set<DubendorfHangarLog> dubendorfHangarLogs = EnumSet.of( //
        DubendorfHangarLog._20180108T152648_5f742add, //
        DubendorfHangarLog._20180108T160752_5f742add, //
        DubendorfHangarLog._20180108T162528_5f742add, //
        DubendorfHangarLog._20180112T103859_9e1d3699, //
        DubendorfHangarLog._20180112T113153_9e1d3699 //
    );
    OfflineProcessing.INSTANCE.handle(dubendorfHangarLogs, () -> new LinmotStatusAnalysis());
  }
}
