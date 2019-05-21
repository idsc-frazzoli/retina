// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/* package */ enum StaticHelper {
  ;
  public static void actionListener(JButton jButton, Thunk thunk, int sleep_ms) {
    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        jButton.setEnabled(false);
        thunk.apply();
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              Thread.sleep(sleep_ms);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
            jButton.setEnabled(true);
          }
        }).start();
      }
    });
  }
}
