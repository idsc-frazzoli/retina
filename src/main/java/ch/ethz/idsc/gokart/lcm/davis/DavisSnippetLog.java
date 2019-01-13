// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.data.GlobalAssert;

/** frame with button that launches a fixed duration of davis data recording
 * followed by a conversion to uzh format */
public class DavisSnippetLog {
  private final int period_ms;
  private final File lcmDir;
  private final File uzhDir;
  // ---
  private final JFrame jFrame = new JFrame("");
  private final JButton jButton = new JButton();
  private final ActionListener actionListener = actionEvent -> {
    jButton.setEnabled(false);
    DavisSnippetRunnable davisSnippetRunnable = createSnippet();
    Thread thread = new Thread(davisSnippetRunnable);
    thread.start();
  };

  /** since the first and last frames may not be received completely the
   * application layer should introduce margins
   * 
   * @param period_ms
   * duration of recording
   * @param lcmDir
   * @param uzhDir */
  public DavisSnippetLog(int period_ms, File lcmDir, File uzhDir) {
    lcmDir.mkdir();
    GlobalAssert.that(lcmDir.isDirectory());
    uzhDir.mkdir();
    GlobalAssert.that(uzhDir.isDirectory());
    // ---
    this.period_ms = period_ms;
    this.lcmDir = lcmDir;
    this.uzhDir = uzhDir;
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 200, 50);
    jButton.setText(String.format("record %d [ms]", period_ms));
    jButton.addActionListener(actionListener);
    jFrame.setContentPane(jButton);
    jFrame.setVisible(true);
  }

  private DavisSnippetRunnable createSnippet() {
    return new DavisSnippetRunnable(period_ms, lcmDir) {
      @Override
      public void callback(File file) {
        DavisLcmLogUzhConvert.of(file, uzhDir); // blocking call
        jButton.setEnabled(true);
      }
    };
  }
}
