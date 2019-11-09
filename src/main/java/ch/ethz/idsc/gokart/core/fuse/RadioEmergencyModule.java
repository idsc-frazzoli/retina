// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;

public class RadioEmergencyModule extends EmergencyModule<RimoPutEvent> {
  private final BinaryLcmClient binaryLcmClient = new BinaryLcmClient(GokartLcmChannel.MCUSB_DIN) {
    @Override
    protected void messageReceived(ByteBuffer byteBuffer) {
      byte value = byteBuffer.get();
      System.out.println(value);
    }
  };

  @Override
  protected void first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    binaryLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    binaryLcmClient.stopSubscriptions();
    RimoSocket.INSTANCE.removePutProvider(this);
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    // TODO
    return RimoPutEvent.OPTIONAL_RIMO_PASSIVE;
  }
}
