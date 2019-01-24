// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

/* package */ class StateAndPath implements MPCNativeInsertable {
  // not used:
  // private static final Unit SCE_PER_SECOND = SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND);
  public final GokartState state;
  public final MPCPathParameter path;

  public StateAndPath(MPCPathParameter path, GokartState state) {
    this.path = path;
    this.state = state;
  }

  public StateAndPath(ByteBuffer byteBuffer) {
    state = new GokartState(byteBuffer);
    path = new MPCPathParameter(byteBuffer);
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    state.insert(byteBuffer);
    path.insert(byteBuffer);
  }

  @Override
  public int length() {
    return path.length() + state.length();
  }

  @Override
  public String toString() {
    return "state and path:\n" + state.toString() + path.toString();
  }
}
