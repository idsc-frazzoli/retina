// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.demo.DubendorfHangarLog;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotStateVariable;
import ch.ethz.idsc.retina.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;

class LinmotStatusAnalysis implements OfflineTableSupplier {
  private final Clip range;
  final TensorBuilder tensorBuilder = new TensorBuilder();
  boolean isFused = false;
  Integer failure_index = null;

  public LinmotStatusAnalysis(Scalar offset) {
    range = Clip.function(offset, offset.add(Quantity.of(0.2, "s")));
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    boolean inWindow = range.isInside(time);
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      if (inWindow) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("<<< GET at time = %s\n", time.map(Round._6)));
        stringBuilder.append(String.format(" statusWord = 0x%04x == %s\n", linmotGetEvent.status_word, linmotGetEvent.getStatusWordBits()));
        LinmotStateVariable lsv = linmotGetEvent.getStateVariable();
        stringBuilder
            .append(String.format(" stateVar = 0x%04x == %s; substate = 0x%02x\n", linmotGetEvent.state_variable, lsv.linmotStateVarMain, lsv.substate));
        stringBuilder.append(String.format(" actual_pos = %d\n", linmotGetEvent.actual_position));
        stringBuilder.append(String.format(" demand_pos = %d\n", linmotGetEvent.demand_position));
        stringBuilder.append(String.format(" windingT_1 = %s\n", linmotGetEvent.getWindingTemperature1().map(Round._1)));
        stringBuilder.append(String.format(" windingT_2 = %s\n", linmotGetEvent.getWindingTemperature2().map(Round._1)));
        System.out.println(stringBuilder);
      }
      final boolean status = linmotGetEvent.isOperational();
      if (!isFused) {
        isFused |= status;
        if (isFused)
          System.out.println("calibrated at " + time);
      }
      // ---
      if (isFused) {
        if (!status && Objects.isNull(failure_index)) {
          // System.out.println("failure at " + time);
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
      if (inWindow) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(">>> PUT at time = %s\n", time.map(Round._6)));
        stringBuilder.append(String.format(" control_word = 0x%04x\n", linmotPutEvent.control_word));
        stringBuilder.append(String.format(" motion_cmd_hdr = 0x%04x\n", linmotPutEvent.motion_cmd_hdr));
        stringBuilder.append(String.format(" target_position = %d\n", linmotPutEvent.target_position));
        stringBuilder.append(String.format(" max_velocity = %d\n", linmotPutEvent.max_velocity));
        stringBuilder.append(String.format(" acceleration = %d\n", linmotPutEvent.acceleration));
        stringBuilder.append(String.format(" deceleration = %d\n", linmotPutEvent.deceleration));
        System.out.println(stringBuilder);
      }
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
    return tensorBuilder.getTensor().extract(failure_index - 60, failure_index + 20);
  }

  private static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    // OfflineProcessing.single( //
    // DubendorfHangarLog._20180108T152648_5f742add.file(LOG_ROOT), //
    // new LinmotStatusAnalysis(Quantity.of(662.747024, "s")), //
    // DubendorfHangarLog._20180108T152648_5f742add.title());
    // OfflineProcessing.single( //
    // DubendorfHangarLog._20180108T162528_5f742add.file(LOG_ROOT), //
    // new LinmotStatusAnalysis(Quantity.of(128.219674, "s")), //
    // DubendorfHangarLog._20180108T162528_5f742add.title());
    OfflineProcessing.single( //
        DubendorfHangarLog._20180112T113153_9e1d3699.file(LOG_ROOT), //
        new LinmotStatusAnalysis(Quantity.of(1576.054329, "s")), //
        DubendorfHangarLog._20180112T113153_9e1d3699.title());
  }
}