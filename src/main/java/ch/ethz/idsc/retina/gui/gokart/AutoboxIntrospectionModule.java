// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.zhkart.AutoboxSocket;
import ch.ethz.idsc.retina.dev.zhkart.AutoboxSockets;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRanks;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

public class AutoboxIntrospectionModule extends AbstractModule {
  private static final int PERIOD_MS = 200; // 200 ms -> 5 Hz
  // ---
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Timer timer = new Timer();
  private final JTextField[] jTextField = new JTextField[4];

  @Override
  protected void first() throws Exception {
    JPanel jPanel = new JPanel(new GridLayout(4, 1));
    int index = 0;
    for (AutoboxSocket<?, ?> autoboxSocket : AutoboxSockets.ALL) {
      jTextField[index] = new JTextField();
      jTextField[index].setToolTipText(autoboxSocket.getClass().getSimpleName());
      jPanel.add(jTextField[index]);
      ++index;
    }
    jFrame.setContentPane(jPanel);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        int index = 0;
        for (AutoboxSocket<?, ?> autoboxSocket : AutoboxSockets.ALL) {
          jTextField[index].setText(autoboxSocket.getPutProviderDesc());
          Optional<ProviderRank> optional = autoboxSocket.getPutProviderRank();
          jTextField[index].setBackground(optional.isPresent() //
              ? ProviderRanks.color(optional.get())
              : Color.WHITE);
          ++index;
        }
      }
    };
    timer.schedule(timerTask, 100, PERIOD_MS);
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
