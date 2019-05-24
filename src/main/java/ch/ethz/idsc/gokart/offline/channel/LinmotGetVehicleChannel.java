// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum LinmotGetVehicleChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelTable
  public String channel() {
    return LinmotLcmServer.CHANNEL_GET;
  }

  @Override
  public String exportName() {
    // TODO JPH use different name!
    return channel();
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    // TODO canonize
    LinmotGetEvent linmotGetEvent = new LinmotGetEvent(byteBuffer);
    return Tensors.of( //
        linmotGetEvent.getActualPosition().map(Magnitude.METER).negate(), //
        linmotGetEvent.getWindingTemperature1().map(Magnitude.DEGREE_CELSIUS), //
        linmotGetEvent.getWindingTemperature2().map(Magnitude.DEGREE_CELSIUS), //
        linmotGetEvent.getDemandPosition().map(Magnitude.METER).negate(), //
        RealScalar.of(linmotGetEvent.statusWord()), //
        RealScalar.of(linmotGetEvent.stateVariable()) //
    );
  }
}
