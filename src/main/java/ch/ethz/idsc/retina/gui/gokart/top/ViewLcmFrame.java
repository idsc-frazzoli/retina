// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.win.TimerFrame;

public class ViewLcmFrame extends TimerFrame {
  public final JButton jButtonStoreMap = new JButton("store map");
  public final JButton jButtonSnap = new JButton("snap to map");

  public ViewLcmFrame() {
    jToolBar.add(jButtonStoreMap);
    jToolBar.add(jButtonSnap);
  }
}
