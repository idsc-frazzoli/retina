//code by em
package ch.ethz.idsc.gokart.lcm.led;

import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

import java.util.Random;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;

public class KittLedModuleCoach extends AbstractClockedModule {
  private int num1;
  private int num2;
  private final int max = 5;
  private final int min = -5;
  //private int indexColor;
  private int[] arrayIndex = new int[11];

  @Override
  protected void runAlgo() {
    Random rand = new Random();
    num1 = rand.nextInt((max - min) + 1) + min ;
    num2 = rand.nextInt((max - min) + 1) + min ;
    //System.out.println("num:"+num1+" num2:"+num2);
    // indexColor = counterProgress % arrayIndex.length;
    for (int i = min; i < max+1; i++) {
      if (num1 == i && num2==i) {
        arrayIndex[i+Math.abs(min)] = 3;
      } else if(num1 == i && num2!=i){
        arrayIndex[i+Math.abs(min)] = 1;
      } else if(num2 == i && num1!=i){
        arrayIndex[i+Math.abs(min)] = 2;
      } 
      else {
        arrayIndex[i+Math.abs(min)] = 0;
      }
    }
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, arrayIndex);
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(1, SI.SECOND);
  }

  @Override
  protected void first() {
    // TODO Auto-generated method stub
  }

  @Override
  protected void last() {
    // TODO Auto-generated method stub
  }
}
