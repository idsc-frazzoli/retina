// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.awt.Color;

import ch.ethz.idsc.retina.util.GlobalAssert;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LEDStatus implements OfflineVectorInterface {
  public static final int NUM_LEDS = 10;
  public static final int LENGTH = 5;

  public final int indexGreen;
  public final int indexRed;
  public final Color statusColor;

  public LEDStatus(int index) {
    this(index, index);
  }

  public LEDStatus(int indexGreen, int indexRed) {
    this(indexGreen, indexRed, Color.BLACK);
  }

  public LEDStatus(int indexGreen, int indexRed, Color statusColor) {
    GlobalAssert.that(indexGreen >= 0 && indexGreen < NUM_LEDS);
    GlobalAssert.that(indexRed >= 0 && indexRed < NUM_LEDS);
    this.indexGreen = indexGreen;
    this.indexRed = indexRed;
    this.statusColor = statusColor;
  }

  public int[] asArray() {
    return new int[] { indexGreen, indexRed, statusColor.getRed(), statusColor.getBlue(), statusColor.getBlue() };
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vectorInt(asArray());
  }
}
