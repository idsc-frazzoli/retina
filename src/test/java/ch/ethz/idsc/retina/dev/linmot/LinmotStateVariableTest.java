// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import junit.framework.TestCase;

public class LinmotStateVariableTest extends TestCase {
  public void testNormal() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 2241);
    // System.out.println(lsv.linmotStateVarMain);
    // System.out.println(lsv.substate);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
  }

  public void testNormal2() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 2240);
    // System.out.println(lsv.linmotStateVarMain);
    // System.out.println(lsv.substate);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
  }

  public void testFailure() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 1027);
    // System.out.println(lsv.linmotStateVarMain);
    // System.out.println(lsv.substate);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.ERROR);
  }

  public void testFailure2() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 512);
    // System.out.println(lsv.linmotStateVarMain);
    // System.out.println(lsv.substate);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.READY_TO_SWITCH_ON);
    assertEquals(lsv.substate, 0);
  }

  public void testFailure3() {
    LinmotStateVariable lsv = new LinmotStateVariable((short) 2176);
    // System.out.println(lsv.linmotStateVarMain);
    // System.out.println(lsv.substate);
    assertEquals(lsv.linmotStateVarMain, LinmotStateVarMain.OPERATION_ENABLED);
    assertEquals(lsv.substate, 128);
  }
}
