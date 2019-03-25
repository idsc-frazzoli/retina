// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.gokart.lcm.BinaryBlobs;
import ch.ethz.idsc.gokart.lcm.BinaryLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.data.BufferInsertable;
import idsc.BinaryBlob;

// TODO JPH/MH split class into client(MPCControlUpdateLcmClient) and publisher
/* package */ abstract class LcmMPCControlClient extends BinaryLcmClient implements StartAndStoppable {
  public static LcmMPCControlClient kinematic() {
    return new LcmMPCControlClient("") {
      @Override
      BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
        return new MPCOptimizationParameterMessageKinematic(mpcOptimizationParameter, mpcNativeSession);
      }
    };
  }

  public static LcmMPCControlClient dynamic() {
    return new LcmMPCControlClient(".d") {
      @Override
      BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession) {
        return new MPCOptimizationParameterMessageDynamic(mpcOptimizationParameter, mpcNativeSession);
      }
    };
  }

  private final List<MPCControlUpdateListener> listeners = new CopyOnWriteArrayList<>();
  private final MPCNativeSession mpcNativeSession = new MPCNativeSession();
  private final BinaryBlobPublisher controlRequestPublisher;
  private final BinaryBlobPublisher optimizationParameterPublisher;
  // TODO design no good. lastcns should not be public. use member function instead
  /* package for testing */ ControlAndPredictionSteps lastcns = null;

  private LcmMPCControlClient(String appendix) {
    super(GokartLcmChannel.MPC_FORCES_CNS);
    controlRequestPublisher = new BinaryBlobPublisher("mpc.forces.gs" + appendix);
    optimizationParameterPublisher = new BinaryBlobPublisher("mpc.forces.op" + appendix);
  }

  /** @param mpcOptimizationParameter
   * @param mpcNativeSession
   * @return */
  abstract BufferInsertable from(MPCOptimizationParameter mpcOptimizationParameter, MPCNativeSession mpcNativeSession);

  @Override // from StartAndStoppable
  public final void start() {
    startSubscriptions();
    mpcNativeSession.first();
  }

  @Override // from StartAndStoppable
  public final void stop() {
    mpcNativeSession.last();
    stopSubscriptions();
  }

  /** send gokart state which starts the mpc optimization with the newest state
   * 
   * @param gokartState the newest available gokart state */
  public final void publishControlRequest(GokartState gokartState, MPCPathParameter mpcPathParameter) {
    ControlRequestMessage gokartStateMessage = new ControlRequestMessage(gokartState, mpcPathParameter, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(gokartStateMessage.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartStateMessage.insert(byteBuffer);
    controlRequestPublisher.accept(binaryBlob);
  }

  /** switch to testing binary that send back test data has to be called before first */
  public final void switchToTest() {
    mpcNativeSession.switchToTest();
  }

  /** switch to mode where binary is no automatically starting */
  public final void switchToExternalStart() {
    mpcNativeSession.switchToExternalStart();
  }

  public final void publishOptimizationParameter(MPCOptimizationParameter mpcOptimizationParameter) {
    BufferInsertable bufferInsertable = from(mpcOptimizationParameter, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(bufferInsertable.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    bufferInsertable.insert(byteBuffer);
    optimizationParameterPublisher.accept(binaryBlob);
  }

  public final void addControlUpdateListener(MPCControlUpdateListener listener) {
    listeners.add(listener);
  }

  @Override // from BinaryLcmClient
  protected final void messageReceived(ByteBuffer byteBuffer) {
    ControlAndPredictionStepsMessage cns = new ControlAndPredictionStepsMessage(byteBuffer);
    for (MPCControlUpdateListener listener : listeners)
      listener.getControlAndPredictionSteps(cns.controlAndPredictionSteps);
    lastcns = cns.controlAndPredictionSteps;
  }
}
