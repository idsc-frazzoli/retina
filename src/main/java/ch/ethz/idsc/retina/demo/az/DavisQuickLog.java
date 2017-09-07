// code by jph
package ch.ethz.idsc.retina.demo.az;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class DavisQuickLog {
  final JFrame jFrame = new JFrame("");
  final JButton jButton = new JButton("record");

  public DavisQuickLog() {
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

  ActionListener actionListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
      jButton.setEnabled(false);
      DavisSnippetRunnable davisSnippetThread = new DavisSnippetRunnable(1000) {
        @Override
        public void callback() {
          jButton.setEnabled(true);
        };
      };
      Thread thread = new Thread(davisSnippetThread);
      thread.start();
    }
  };

  public static void main(String[] args) {
    new DavisQuickLog();
  }
}
