// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

public class LinmotStateVariable {
  public final LinmotStateVarMain linmotStateVarMain;
  public final int substate;

  public LinmotStateVariable(short state_variable) {
    int hi = state_variable & 0xff00;
    substate = state_variable & 0xff;
    linmotStateVarMain = LinmotStateVarMain.values()[hi >> 8];
  }
}
