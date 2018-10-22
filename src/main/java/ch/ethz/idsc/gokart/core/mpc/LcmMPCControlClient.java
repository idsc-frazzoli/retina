// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.lcm.BinaryBlobs;
import ch.ethz.idsc.retina.lcm.BinaryLcmClient;
import idsc.BinaryBlob;

public class LcmMPCControlClient extends BinaryLcmClient implements MPCControlClient {
  private List<MPCControlUpdateListener> listeners = new ArrayList<>();
  MPCNativeSession mpcNativeSession = new MPCNativeSession();
  private final BinaryBlobPublisher gokartStatePublisher = new BinaryBlobPublisher("mpc.forces.gs");
  private final BinaryBlobPublisher pathParameterPublisher = new BinaryBlobPublisher("mpc.forces.pp");
  private final BinaryBlobPublisher optimizationParameterPublisher = new BinaryBlobPublisher("mpc.forces.op");

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

  public void publishGokartState(GokartState gokartState) {
    GokartStateMessage gokartStateMessage = new GokartStateMessage(gokartState, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(gokartStateMessage.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    gokartStateMessage.insert(byteBuffer);
    gokartStatePublisher.accept(binaryBlob);
  }

  public void publishPathParameter(MPCPathParameter mpcPathParameter) {
    MPCPathParameterMessage mpcPathParameterMessage = new MPCPathParameterMessage(mpcPathParameter, mpcNativeSession);
    BinaryBlob binaryBlob = BinaryBlobs.create(mpcPathParameterMessage.length());
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    mpcPathParameterMessage.insert(byteBuffer);
    pathParameterPublisher.accept(binaryBlob);
  }

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
    System.out.println(cns.controlAndPredictionSteps.steps[0]);
    for (MPCControlUpdateListener listener : listeners) {
      listener.getControlAndPredictionSteps(cns.controlAndPredictionSteps);
    }
  }

  @Override
  protected String channel() {
    return "mpc.forces.cns";
  }
}
