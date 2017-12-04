// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import junit.framework.TestCase;

public class ParametersComponentTest extends TestCase {
  public void testSimple() {
    ParametersComponent pc = new ParametersComponent(SteerConfig.GLOBAL);
    JFrame jFrame = new JFrame();
    jFrame.setContentPane(pc.getScrollPane());
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 400, 300);
  }
}
