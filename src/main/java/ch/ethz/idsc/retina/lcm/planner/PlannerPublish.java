// code by ynager
package ch.ethz.idsc.retina.lcm.planner;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.io.Primitives;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum PlannerPublish {
  ;
  public static void publishTrajectory(List<TrajectorySample> trajectory) {
    if (!trajectory.isEmpty()) {
      // publish trajectory states & times
      BinaryBlob binaryBlob = new BinaryBlob();
      final int stateDim = trajectory.get(0).stateTime().state().length();
      final int dataLength = trajectory.size() * (stateDim + 1) * Float.BYTES;
      System.out.print(trajectory.size());
      binaryBlob.data = new byte[dataLength];
      binaryBlob.data_length = dataLength;
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      for (TrajectorySample ts : trajectory) {
        float[] data = Primitives.toFloatArray(ts.stateTime().state());
        for (float value : data)
          byteBuffer.putFloat(value);
        byteBuffer.putFloat(ts.stateTime().time().number().floatValue());
      }
      LCM.getSingleton().publish(GokartLcmChannel.TRAJECTORY_STATETIME, binaryBlob);
    }
  }
}
