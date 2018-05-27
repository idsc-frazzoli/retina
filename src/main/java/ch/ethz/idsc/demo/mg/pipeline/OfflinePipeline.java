// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// provide an offline pipeline "wrapper" with visualization
public class OfflinePipeline implements OfflineLogListener{

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    // TODO Auto-generated method stub
    
  }
}
