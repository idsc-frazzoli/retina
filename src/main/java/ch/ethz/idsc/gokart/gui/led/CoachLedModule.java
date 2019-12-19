// code by em
package ch.ethz.idsc.gokart.gui.led;

import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

import java.util.Random;
import java.util.stream.IntStream;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;

public class CoachLedModule extends AbstractClockedModule {
  private final int[] arrayIndex = new int[VirtualLedModule.NUM_LEDS];
  private final int max = (int) Math.floor(arrayIndex.length / 2.);
  private final int min = -max;

  @Override
  protected void runAlgo() {
    Random rand = new Random();
    int num1 = rand.nextInt((max - min) + 1) + min;
    int num2 = rand.nextInt((max - min) + 1) + min;
    // System.out.println("num: " + num1 + " num2: " + num2);
    IntStream.rangeClosed(min, max).forEach(i -> //
        arrayIndex[Math.floorMod(i, arrayIndex.length)] = (num1 == i ? 1 : 0) + (num2 == i ? 2 : 0));
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, arrayIndex);
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(1, SI.SECOND);
  }

  @Override
  protected void first() {
    // ---
  }

  @Override
  protected void last() {
    // ---
  }
}
