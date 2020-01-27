// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import ch.ethz.idsc.gokart.lcm.led.LEDLcmClient;
import ch.ethz.idsc.gokart.lcm.led.LEDListener;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

// TODO attach to autorun
public class LEDServerModule extends AbstractModule implements LEDListener {
  private final LEDLcmClient ledLcmClient = new LEDLcmClient();

  @Override // from AbstractModule
  protected void first() {
    LEDSocket.INSTANCE.start();
    ledLcmClient.addListener(this);
    ledLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    ledLcmClient.stopSubscriptions();
    LEDSocket.INSTANCE.stop();
  }

  @Override
  public void statusReceived(LEDStatus ledStatus) {
    // TODO process and send to LEDSocket
    // either use CRC-8-maxim or ask mac/markus to turn it off
  }
}
