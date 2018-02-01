// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

class LinmotStatusAnalysis implements OfflineTableSupplier {
  private boolean status = false;
  final TensorBuilder tensorBuilder = new TensorBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LinmotLcmServer.CHANNEL_GET)) {
      LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
      if (status != linmotGetEvent.isOperational()) {
        status = linmotGetEvent.isOperational();
        System.out.println(time.number().doubleValue() + " " + status);
      }
      tensorBuilder.append(Tensors.vector( //
          time.number().doubleValue(), //
          linmotGetEvent.status_word, //
          linmotGetEvent.state_variable, //
          linmotGetEvent.actual_position, //
          linmotGetEvent.demand_position, //
          linmotGetEvent.getWindingTemperature1().number().doubleValue(), //
          linmotGetEvent.getWindingTemperature2().number().doubleValue() //
      ));
    }
  }

  @Override
  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.INSTANCE.handle(new LinmotStatusAnalysis());
  }
}
