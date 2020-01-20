// code by em
package ch.ethz.idsc.gokart.gui.led;

import ch.ethz.idsc.gokart.lcm.led.LEDLcmClient;
import ch.ethz.idsc.gokart.lcm.led.LEDListener;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.stream.IntStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;

public class VirtualLedModule extends AbstractModule implements LEDListener {
  public static final int NUM_LEDS = 10; // TODO delete once actual LED number is known and provided elsewhere
  private static final ColorDataIndexed COLOR_SCHEME = ColorDataLists._001.cyclic();

  private final LEDLcmClient ledLcmClient = new LEDLcmClient();
  private final JFrame jFrame = new JFrame();
  private final JTextField[] leds = IntStream.range(0, NUM_LEDS).mapToObj(i -> new JTextField()).toArray(JTextField[]::new);
  private final WindowConfiguration windowConfiguration = AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(1, NUM_LEDS));
      for (JTextField led : leds) {
        led.setBackground(COLOR_SCHEME.getColor(0));
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
      Color color = COLOR_SCHEME.getColor(index);
      led.setBackground(color);
    }
  }
}
