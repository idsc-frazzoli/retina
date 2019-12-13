// code by em
package ch.ethz.idsc.gokart.lcm.led;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

import java.awt.Color;

import javax.swing.JFrame;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class KittColourLedModule extends AbstractModule implements LEDListener {
  private final LEDLcmClient ledLcmClient = new LEDLcmClient();
  private int[] indexColour;
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  public KittColourLedModule() {
    indexColour = new int[4];
  }

  @Override
  protected void first() {
    ledLcmClient.addListener(this);
    ledLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    ledLcmClient.stopSubscriptions();
  }

  /** its arrayReceived turns int[] into Colors using ColorDataIndexed, and sets them accordingly in the GUI */
  @Override
  public void arrayReceived(int[] indexColour) {
    for (int i = 0; i < indexColour.length; i++) {
    //   colour[i] = ColorDataIndexed.getColor(i);
    }
  }
}
