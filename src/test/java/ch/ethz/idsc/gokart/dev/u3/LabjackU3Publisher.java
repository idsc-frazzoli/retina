// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;

public enum LabjackU3Publisher {
  ;
  private static final BinaryBlobPublisher BINARY_BLOB_PUBLISHER = //
      new BinaryBlobPublisher(GokartLcmChannel.LABJACK_U3_ADC);

  public static void accept(LabjackAdcFrame labjackAdcFrame) {
    byte[] array = labjackAdcFrame.asArray();
    BINARY_BLOB_PUBLISHER.accept(array, array.length);
  }
}
