// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcListener;
import ch.ethz.idsc.retina.dev.u3.LabjackU3LiveProvider;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LabjackU3LcmModule extends AbstractModule implements LabjackAdcListener {
  private final LabjackU3LiveProvider labjackU3LiveProvider = new LabjackU3LiveProvider(this);
  private final BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.LABJACK_U3_ADC);

  @Override // from AbstractModule
  protected void first() throws Exception {
    labjackU3LiveProvider.start();
  }

  @Override // from AbstractModule
  protected void last() {
    labjackU3LiveProvider.stop();
  }

  @Override // from LabjackAdcListener
  public void labjackAdc(LabjackAdcFrame labjackAdcFrame) {
    byte[] array = labjackAdcFrame.asArray();
    binaryBlobPublisher.accept(array, array.length);
  }
  // public static void main(String[] args) throws Exception {
  // new LabjackU3LcmModule().first();
  // }
}
