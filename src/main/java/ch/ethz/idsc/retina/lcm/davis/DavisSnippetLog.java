// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.GlobalAssert;

public class DavisSnippetLog {
  private final int period_ms;
  private final File lcmDir;
  private final File directory;
  // ---
  private final JFrame jFrame = new JFrame("");
  private final JButton jButton = new JButton("record");
  private final ActionListener actionListener = actionEvent -> {
    jButton.setEnabled(false);
    DavisSnippetRunnable davisSnippetRunnable = createSnippet();
    Thread thread = new Thread(davisSnippetRunnable);
    thread.start();
  };

  public DavisSnippetLog(int period_ms, File lcmDir, File directory) {
    lcmDir.mkdir();
    GlobalAssert.that(lcmDir.isDirectory());
    directory.mkdir();
    GlobalAssert.that(directory.isDirectory());
    // ---
    this.period_ms = period_ms;
    this.lcmDir = lcmDir;
    this.directory = directory;
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 200, 100);
    {
      JPanel jPanel = new JPanel(new BorderLayout());
      jButton.addActionListener(actionListener);
      jPanel.add(jButton, BorderLayout.NORTH);
      jFrame.setContentPane(jPanel);
    }
    jFrame.setVisible(true);
  }

  private DavisSnippetRunnable createSnippet() {
    return new DavisSnippetRunnable(period_ms, lcmDir) {
      @Override
      public void callback(File file) {
        DavisLcmLogConvert.of(file, directory); // blocking call
        jButton.setEnabled(true);
      };
    };
  }
}
