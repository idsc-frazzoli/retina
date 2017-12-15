// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.win.TimerFrame;

public class ViewLcmFrame extends TimerFrame {
  public final JButton jButton = new JButton("store map");

  public ViewLcmFrame() {
    jToolBar.add(jButton);
  }
}
