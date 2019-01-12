// code by jph
package ch.ethz.idsc.gokart.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.AutoboxSocket;
import ch.ethz.idsc.gokart.core.AutoboxSockets;
import ch.ethz.idsc.gokart.core.ProviderRanks;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class AutoboxIntrospectionModule extends AbstractModule {
  private static final int NUMEL = 4;
  private static final int LENGTH = 3;
  private static final int PERIOD_MS = 200; // 200 ms -> 5 Hz
  // ---
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final Timer timer = new Timer();
  private final JTextField[] jTextField = new JTextField[NUMEL];
  private final JLabel[][] jLabel = new JLabel[NUMEL][LENGTH];

  @Override // from AbstractModule
  protected void first() throws Exception {
    JPanel jPanel = new JPanel(new BorderLayout());
    { // title
      JPanel jPanelTitle = new JPanel(new GridLayout(NUMEL, 1));
      for (AutoboxSocket<?, ?> autoboxSocket : AutoboxSockets.ALL) {
        String title = autoboxSocket.getClass().getSimpleName();
        jPanelTitle.add(new JLabel(title.substring(0, title.length() - 6)));
      }
      jPanel.add(BorderLayout.WEST, jPanelTitle);
    }
    JPanel jPanelTextField = new JPanel(new GridLayout(NUMEL, 1));
    { // provider
      int index = 0;
      for (AutoboxSocket<?, ?> autoboxSocket : AutoboxSockets.ALL) {
        jTextField[index] = new JTextField();
        jTextField[index].setEditable(false);
        jTextField[index].setToolTipText(autoboxSocket.getClass().getSimpleName());
        jPanelTextField.add(jTextField[index]);
        ++index;
      }
      jPanel.add(BorderLayout.CENTER, jPanelTextField);
    }
    JPanel jPanelCounts = new JPanel(new GridLayout(NUMEL, LENGTH));
    { // size of providers and listeners
      for (int index = 0; index < NUMEL; ++index)
        for (int count = 0; count < jLabel[index].length; ++count) {
          jLabel[index][count] = new JLabel("?", SwingConstants.CENTER);
          jLabel[index][count].setPreferredSize(new Dimension(24, 18));
          jPanelCounts.add(jLabel[index][count]);
        }
      jPanel.add(BorderLayout.EAST, jPanelCounts);
    }
    jFrame.setContentPane(jPanel);
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        int index = 0;
        for (AutoboxSocket<?, ?> autoboxSocket : AutoboxSockets.ALL) {
          {
            jTextField[index].setText(autoboxSocket.getPutProviderDesc());
            Optional<ProviderRank> optional = autoboxSocket.getPutProviderRank();
            jTextField[index].setBackground(optional.isPresent() //
                ? ProviderRanks.color(optional.get())
                : Color.WHITE);
          }
          jLabel[index][0].setText("" + autoboxSocket.getPutProviderSize());
          jLabel[index][1].setText("" + autoboxSocket.getPutListenersSize());
          jLabel[index][2].setText("" + autoboxSocket.getGetListenersSize());
          ++index;
        }
      }
    };
    timer.schedule(timerTask, 100, PERIOD_MS);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        timer.cancel();
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  /***************************************************/
  public static void standalone() throws Exception {
    AutoboxIntrospectionModule autoboxTestingModule = new AutoboxIntrospectionModule();
    autoboxTestingModule.first();
    autoboxTestingModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
