// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class Vmu931ReplyTest extends TestCase {
  public void testSimple() {
    List<Vmu931Reply> list = new LinkedList<>();
    assertFalse(list.contains(Vmu931Reply.CALIBRATION));
    Vmu931Reply.match("Calibration completed.", list::add);
    assertTrue(list.contains(Vmu931Reply.CALIBRATION));
  }
}
