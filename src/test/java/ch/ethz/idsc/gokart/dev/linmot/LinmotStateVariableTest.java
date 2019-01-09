// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import junit.framework.TestCase;

public class LinmotStateVariableTest extends TestCase {
  public void testNormal() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 2241);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
  }

  public void testNormal2() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 2240);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
  }

  public void testFailure() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 1027);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.ERROR);
  }

  public void testFailure2() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 512);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.READY_TO_SWITCH_ON);
    assertEquals(lsv.substate, 0);
  }

  public void testFailure3() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 2176);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
    assertEquals(lsv.substate, 128);
  }
}
