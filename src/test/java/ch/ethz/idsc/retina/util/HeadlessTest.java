// code by jph
package ch.ethz.idsc.retina.util;

import javax.swing.JFrame;
import javax.swing.JLabel;

import junit.framework.TestCase;

public class HeadlessTest extends TestCase {
  public void testSimple() throws Exception {
    JFrame jFrame = new JFrame();
    jFrame.setBounds(100, 100, 300, 100);
    jFrame.setContentPane(new JLabel("test"));
    jFrame.setVisible(true);
    Thread.sleep(100);
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
