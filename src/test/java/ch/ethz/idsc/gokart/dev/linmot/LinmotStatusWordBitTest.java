// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.Set;

import junit.framework.TestCase;

public class LinmotStatusWordBitTest extends TestCase {
  public void test20180108T152648Stage1() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 16562);
    assertEquals(set.toString(), //
        "[SWITCH_ON, VOLTAGE, QUICK_STEP, WARNING, IN_RANGE1]");
  }

  /** ===========================================
   * 20180108T162528_5f742add.lcm.00
   * ============================================ */
  public void test20180108T162528Stage1() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 16560);
    assertEquals(set.toString(), //
        "[VOLTAGE, QUICK_STEP, WARNING, IN_RANGE1]");
  }

  public void test20180108T162528Stage2() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 16570);
    assertEquals(set.toString(), //
        "[SWITCH_ON, ERROR, VOLTAGE, QUICK_STEP, WARNING, IN_RANGE1]");
  }

  /** ===========================================
   * 20180112T103859_9e1d3699.lcm.00
   * ============================================ */
  public void test20180112T103859Stage1() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 19511);
    assertEquals(set.toString(), //
        "[OPERATION_ENABLED, SWITCH_ON, OPERATION, VOLTAGE, QUICK_STEP, IN_POSITION, HOMED, IN_RANGE1]");
  }

  public void test20180112T103859Stage2() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 18487);
    assertEquals(set.toString(), //
        "[OPERATION_ENABLED, SWITCH_ON, OPERATION, VOLTAGE, QUICK_STEP, HOMED, IN_RANGE1]");
  }

  public void test20180112T103859Stage3() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 2096);
    assertEquals(set.toString(), //
        "[VOLTAGE, QUICK_STEP, HOMED]");
  }

  public void test20180112T103859Stage4() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 18480); // 18480
    assertEquals(set.toString(), //
        "[VOLTAGE, QUICK_STEP, HOMED, IN_RANGE1]");
  }

  public void test20180112T103859Stage5() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 18490);
    assertEquals(set.toString(), //
        "[SWITCH_ON, ERROR, VOLTAGE, QUICK_STEP, HOMED, IN_RANGE1]");
  }

  /** ===========================================
   * 20180112T113153_9e1d3699.lcm.00
   * ============================================ */
  public void test20180112T113153Stage1() {
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 19511);
    assertEquals(set.toString(), //
        "[OPERATION_ENABLED, SWITCH_ON, OPERATION, VOLTAGE, QUICK_STEP, IN_POSITION, HOMED, IN_RANGE1]");
  }

  public void test20180112T113153Stage2() { // warning
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 19639);
    assertEquals(set.toString(), //
        "[OPERATION_ENABLED, SWITCH_ON, OPERATION, VOLTAGE, QUICK_STEP, WARNING, IN_POSITION, HOMED, IN_RANGE1]");
  }

  public void test20180112T113153Stage3() { // warning
    Set<LinmotStatusWordBit> set = LinmotStatusWordBit.from((short) 18682);
    assertEquals(set.toString(), //
        "[SWITCH_ON, ERROR, VOLTAGE, QUICK_STEP, SWITCH_ON_LOCK, WARNING, HOMED, IN_RANGE1]");
  }
}
