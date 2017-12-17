// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.retina.gui.gokart.GokartStatusEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** class design is similar to {@link GokartStatusEvent} */
public class GokartPoseEvent extends DataEvent implements GokartPoseInterface {
  private static final int LENGTH = 8 * 3;
  // ---
  private final double x;
  private final double y;
  private final double angle;

  public GokartPoseEvent(Tensor pose) {
    x = pose.Get(0).number().doubleValue(); // TODO use SI extraction
    y = pose.Get(1).number().doubleValue();
    angle = pose.Get(2).number().doubleValue();
  }

  public GokartPoseEvent(ByteBuffer byteBuffer) {
    x = byteBuffer.getDouble();
    y = byteBuffer.getDouble();
    angle = byteBuffer.getDouble();
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putDouble(x);
    byteBuffer.putDouble(y);
    byteBuffer.putDouble(angle);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from GokartPoseInterface
  public Tensor getPose() {
    return Tensors.of(Quantity.of(x, "m"), Quantity.of(y, "m"), DoubleScalar.of(angle));
  }

  @Override // from GokartPoseInterface
  public void setPose(Tensor pose) {
    throw new RuntimeException();
  }
}
