// code by em
package ch.ethz.idsc.gokart.gui.led;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;
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
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class VirtualLedModule extends AbstractModule implements LEDListener {

  private final LEDLcmClient ledLcmClient = new LEDLcmClient();
  private final JFrame jFrame = new JFrame();
  private final JTextField statusLed = new JTextField();
  private final JTextField[] leds = IntStream.range(0, LEDStatus.NUM_LEDS).mapToObj(i -> new JTextField()).toArray(JTextField[]::new);
  private final WindowConfiguration windowConfiguration = AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(2, leds.length));
      {
        statusLed.setEnabled(false);
        statusLed.setBackground(Color.BLACK);
        IntStream.range(0, leds.length).forEach(i -> jPanel.add(i == 0 ? statusLed : new JSeparator()));
      }
      {
        for (JTextField led : leds) {
          led.setEnabled(false);
          led.setBackground(Color.BLACK);
          jPanel.add(led);
        }
      }
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
    ledLcmClient.addListener(this);
    ledLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
    // ---
    ledLcmClient.stopSubscriptions();
  }

  @Override // from LEDListener
  public void statusReceived(LEDStatus ledStatus) {
    statusLed.setBackground(ledStatus.statusColor);
    for (JTextField led : leds)
      led.setBackground(Color.BLACK);

    if (ledStatus.indexGreen == ledStatus.indexRed)
      leds[ledStatus.indexGreen].setBackground(Color.BLUE);
    else {
      leds[ledStatus.indexGreen].setBackground(Color.GREEN);
      leds[ledStatus.indexRed].setBackground(Color.RED);
    }
  }
}
