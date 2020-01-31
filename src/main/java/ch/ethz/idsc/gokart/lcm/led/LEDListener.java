// code by gjoel
package ch.ethz.idsc.gokart.lcm.led;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;

public interface LEDListener {
  void statusReceived(LEDStatus ledStatus);
}
