// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.BufferInsertable;

/* package */ class StateAndPath implements BufferInsertable {
  private final GokartState gokartState;
  private final MPCPathParameter mpcPathParameter;

  public StateAndPath(GokartState gokartState, MPCPathParameter mpcPathParameter) {
    this.gokartState = gokartState;
    this.mpcPathParameter = mpcPathParameter;
  }

  public StateAndPath(ByteBuffer byteBuffer) {
    gokartState = new GokartState(byteBuffer);
    mpcPathParameter = new MPCPathParameter(byteBuffer);
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    gokartState.insert(byteBuffer);
    mpcPathParameter.insert(byteBuffer);
  }

  @Override // from BufferInsertable
  public int length() {
    return GokartState.LENGTH + mpcPathParameter.length();
  }

  @Override // from Object
  public String toString() {
    return "state and path:\n" + gokartState.toString() + mpcPathParameter.toString();
  }
}
