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
import idsc.BinaryBlob;

public class LcmMPCControlClient extends BinaryLcmClient implements MPCControlClient {
  private final List<MPCControlUpdateListener> listeners = new CopyOnWriteArrayList<>();
  private final MPCNativeSession mpcNativeSession = new MPCNativeSession();
  private final BinaryBlobPublisher controlRequestPublisher = new BinaryBlobPublisher("mpc.forces.gs");
  private final BinaryBlobPublisher optimizationParameterPublisher = new BinaryBlobPublisher("mpc.forces.op");
  // TODO design no good. lastcns should not be public. use member function instead
  public ControlAndPredictionSteps lastcns = null;

  public LcmMPCControlClient() {
    super(GokartLcmChannel.MPC_FORCES_CNS);
  }

  @Override
  public void start() {
    startSubscriptions();
    mpcNativeSession.first();
  }

  @Override
  public void stop() {
    mpcNativeSession.last();
    stopSubscriptions();
  }

  /** send gokart state which starts the mpc optimization with the newest state
   * 
   * @param gokartState the newest available gokart state */
  public void publishControlRequest(GokartState gokartState, MPCPathParameter mpcPathParameter) {
    ControlRequestMessage gokartStateMessage = new ControlRequestMessage(gokartState, mpcPathParameter, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(gokartStateMessage.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartStateMessage.insert(byteBuffer);
    controlRequestPublisher.accept(binaryBlob);
  }

  /** switch to testing binary that send back test data has to be called before first */
  public void switchToTest() {
    mpcNativeSession.switchToTest();
  }

  /** switch to mode where binary is no automatically starting */
  public void switchToExternalStart() {
    mpcNativeSession.switchToExternalStart();
  }

  /* public void publishPathParameter(MPCPathParameter mpcPathParameter) {
   * MPCPathParameterMessage mpcPathParameterMessage = new MPCPathParameterMessage(mpcPathParameter, mpcNativeSession);
   * BinaryBlob binaryBlob = BinaryBlobs.create(mpcPathParameterMessage.length());
   * ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
   * byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
   * mpcPathParameterMessage.insert(byteBuffer);
   * pathParameterPublisher.accept(binaryBlob);
   * } */
  public void publishOptimizationParameter(MPCOptimizationParameter mpcOptimizationParameter) {
    MPCOptimizationParameterMessage mpcOptimizationParameterMessage = new MPCOptimizationParameterMessage(mpcOptimizationParameter, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(mpcOptimizationParameterMessage.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    mpcOptimizationParameterMessage.insert(byteBuffer);
    optimizationParameterPublisher.accept(binaryBlob);
  }

  public void registerControlUpdateLister(MPCControlUpdateListener listener) {
    listeners.add(listener);
  }

  @Override
  protected void messageReceived(ByteBuffer byteBuffer) {
    // get new message
    ControlAndPredictionStepsMessage cns = new ControlAndPredictionStepsMessage(byteBuffer);
    // System.out.println(cns.controlAndPredictionSteps.steps[0]);
    for (MPCControlUpdateListener listener : listeners)
      listener.getControlAndPredictionSteps(cns.controlAndPredictionSteps);
    lastcns = cns.controlAndPredictionSteps;
  }
}
