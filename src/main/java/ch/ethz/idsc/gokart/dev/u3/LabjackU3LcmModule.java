// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.u3.LabjackAdcListener;
import ch.ethz.idsc.retina.u3.LabjackU3LiveProviders;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** reads out labjack u3 device and publishes status of ADC to lcm */
public class LabjackU3LcmModule extends AbstractModule implements LabjackAdcListener {
  private static final BinaryBlobPublisher BINARY_BLOB_PUBLISHER = //
      new BinaryBlobPublisher(GokartLcmChannel.LABJACK_U3_ADC);

  public static void accept(LabjackAdcFrame labjackAdcFrame) {
    byte[] array = labjackAdcFrame.asArray();
    BINARY_BLOB_PUBLISHER.accept(array, array.length);
  }

  // ---
  private final StartAndStoppable labjackU3LiveProvider = LabjackU3LiveProviders.create(this);

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
    accept(labjackAdcFrame);
  }
}
