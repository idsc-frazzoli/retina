// code by jph
package ch.ethz.idsc.retina.demo.az;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class DavisSnippetLog {
  private static final int PERIOD_MS = 1000;
  // ---
  private final JFrame jFrame = new JFrame("");
  private final JButton jButton = new JButton("record");
  private final ActionListener actionListener = actionEvent -> {
    jButton.setEnabled(false);
    DavisSnippetRunnable davisSnippetRunnable = new DavisSnippetRunnable(PERIOD_MS) {
      @Override
      public void callback() {
        jButton.setEnabled(true);
      };
    };
    Thread thread = new Thread(davisSnippetRunnable);
    thread.start();
  };

  public DavisSnippetLog() {
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

  public static void main(String[] args) {
    new DavisSnippetLog();
  }
}
