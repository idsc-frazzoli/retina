// code by gjoel, em
package ch.ethz.idsc.gokart.lcm.led;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.SimpleLcmClient;

public class LEDLcmClient extends SimpleLcmClient<LEDListener>{

  public LEDLcmClient() {
    super(GokartLcmChannel.LED_STATUS);
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    LEDStatus ledStatus = LEDLcm.decode(byteBuffer);
    listeners.forEach(listener -> listener.statusReceived(ledStatus));
  }
}
