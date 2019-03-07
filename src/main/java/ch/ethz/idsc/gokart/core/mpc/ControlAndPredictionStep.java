// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class ControlAndPredictionStep implements BufferInsertable {
  public static final int LENGTH = GokartControl.LENGTH + GokartState.LENGTH;
  // ---
  final GokartControl gokartControl;
  final GokartState gokartState;

  public ControlAndPredictionStep(GokartControl gokartControl, GokartState gokartState) {
    this.gokartControl = gokartControl;
    this.gokartState = gokartState;
  }

  public ControlAndPredictionStep(ByteBuffer byteBuffer) {
    gokartControl = new GokartControl(byteBuffer);
    gokartState = new GokartState(byteBuffer);
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    gokartControl.insert(byteBuffer);
    gokartState.insert(byteBuffer);
  }

  @Override
  public int length() {
    return LENGTH;
  }

  @Override
  public String toString() {
    return "cns:\n" + gokartControl.toString() + gokartState.toString();
  }
}
