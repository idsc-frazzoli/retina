// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.GridLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;
import ch.ethz.idsc.retina.dev.zhkart.AutoboxSockets;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

public class AutboxProviderModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Timer timer = new Timer();
  private final JTextField[] jTextField = new JTextField[4];

  @Override
  protected void first() throws Exception {
    JPanel jPanel = new JPanel(new GridLayout(4, 1));
    for (int index = 0; index < 4; ++index) {
      jTextField[index] = new JTextField();
      jPanel.add(jTextField[index]);
    }
    jFrame.setContentPane(jPanel);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        int index = 0;
        for (AutoboxSocket<?, ?> autoboxSocket : AutoboxSockets.ALL) {
          jTextField[index].setText(autoboxSocket.getPutProviderDesc());
          ++index;
        }
      }
    };
    timer.schedule(timerTask, 100, 200);
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
    timer.cancel();
  }
}
