// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

public class ControlAndPredictionStep implements MPCNativeInsertable {
  // not used:
  // private static final Unit SCE_PER_SECOND = SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND);
  public final GokartState state;
  public final GokartControl control;

  public ControlAndPredictionStep(GokartControl control, GokartState state) {
    this.control = control;
    this.state = state;
  }

  public ControlAndPredictionStep(ByteBuffer byteBuffer) {
    control = new GokartControl(byteBuffer);
    state = new GokartState(byteBuffer);
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    control.insert(byteBuffer);
    state.insert(byteBuffer);
  }

  @Override
  public int length() {
    return control.length() + state.length();
  }

  @Override
  public String toString() {
    return "cns:\n" + control.toString() + state.toString();
  }
}
