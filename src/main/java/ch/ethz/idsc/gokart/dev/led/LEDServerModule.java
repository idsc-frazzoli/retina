// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import ch.ethz.idsc.retina.util.sys.AbstractModule;

public class LEDServerModule extends AbstractModule {

  @Override // from AbstractModule
  protected void first() {
    try {
      LEDSerialSocket.INSTANCE.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
    LEDSocket.INSTANCE.start();
  }

  @Override // from AbstractModule
  protected void last() {
    try {
      LEDSerialSocket.INSTANCE.stop();
    } catch (Exception e) {
      e.printStackTrace();
    }
    LEDSocket.INSTANCE.stop();
  }
}
