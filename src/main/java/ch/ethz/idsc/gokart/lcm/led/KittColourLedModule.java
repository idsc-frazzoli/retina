// code by em
package ch.ethz.idsc.gokart.lcm.led;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class KittColourLedModule extends AbstractModule implements LEDListener {
  private final LEDLcmClient ledLcmClient = new LEDLcmClient();
  private final ColorDataIndexed colorDataIndexed = ColorDataLists._001.cyclic();
  private final JFrame jFrame = new JFrame();
  private final JTextField[] leds = { new JTextField(), new JTextField(), new JTextField(), new JTextField() };
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(4, 1));
      for (JTextField led : leds) {
        led.setBackground(colorDataIndexed.getColor(0));
        jPanel.add(led);
      }
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
    ledLcmClient.addListener(this);
    ledLcmClient.startSubscriptions();
  }

  @Override
  protected void last() {
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
    // ---
    ledLcmClient.stopSubscriptions();
  }

  /** its arrayReceived turns int[] into Colors using ColorDataIndexed, and sets them accordingly in the GUI */
  @Override
  public void arrayReceived(int[] indexColor) {
    for (int i = 0; i < indexColor.length; i++) {
      int index = indexColor[i];
      JTextField led = leds[i];
      Color color = colorDataIndexed.getColor(index);
      System.out.println("color" + color);
      led.setBackground(color);
    }
    
  }
}
