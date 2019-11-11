// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.BinaryBlobs;
import ch.ethz.idsc.retina.util.data.BufferInsertable;
import idsc.BinaryBlob;

/* package */ abstract class MPCRequestPublisher {
  public static MPCRequestPublisher kinematic() {
    return new MPCRequestPublisher("") {
      @Override // from MPCRequestPublisher
      BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
        return new MPCOptimizationParameterMessageKinematic( //
            mpcNativeSession, (MPCOptimizationParameterKinematic) mpcOptimizationParameter);
      }
    };
  }

  public static MPCRequestPublisher dynamic() {
    return new MPCRequestPublisher(".d") {
      @Override // from MPCRequestPublisher
      BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
        return new MPCOptimizationParameterMessageDynamic( //
            mpcNativeSession, (MPCOptimizationParameterDynamic) mpcOptimizationParameter);
      }
    };
  }
  
  public static MPCRequestPublisher ludic() {
    return new MPCRequestPublisher(".l") {
      @Override // from MPCRequestPublisher
      BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
        return new MPCOptimizationParameterMessageLudic( //
            mpcNativeSession, (MPCOptimizationParameterLudic) mpcOptimizationParameter);
      }
    };
  }

  // ---
  private final MPCNativeSession mpcNativeSession = new MPCNativeSession();
  private final BinaryBlobPublisher controlRequestPublisher;
  private final BinaryBlobPublisher optimizationParameterPublisher;

  private MPCRequestPublisher(String appendix) {
    controlRequestPublisher = new BinaryBlobPublisher("mpc.forces.gs" + appendix);
    optimizationParameterPublisher = new BinaryBlobPublisher("mpc.forces.op" + appendix);
  }

  /** @param mpcOptimizationParameter
   * @param mpcNativeSession
   * @return */
  abstract BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession);

  /** send gokart state which starts the mpc optimization with the newest state
   * 
   * @param gokartState the newest available gokart state */
  public final void publishControlRequest(GokartState gokartState, MPCPathParameter mpcPathParameter) {
    ControlRequestMessage gokartStateMessage = new ControlRequestMessage(mpcNativeSession, gokartState, mpcPathParameter);
    BinaryBlob binaryBlob = BinaryBlobs.create(gokartStateMessage.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartStateMessage.insert(byteBuffer);
    controlRequestPublisher.accept(binaryBlob);
  }

  public final void publishOptimizationParameter(MPCOptimizationParameter mpcOptimizationParameter) {
    BufferInsertable bufferInsertable = from(mpcOptimizationParameter, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(bufferInsertable.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    bufferInsertable.insert(byteBuffer);
    optimizationParameterPublisher.accept(binaryBlob);
  }
}
