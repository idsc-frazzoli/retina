// code by jph
package ch.ethz.idsc.retina.offline.slam;

import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class OfflineLocalize implements LidarRayBlockListener {
  protected final TensorBuilder tensorBuilder = new TensorBuilder();
  protected Scalar time;
  public final Tensor skipped = Tensors.empty();

  public void setTime(Scalar time) {
    this.time = time;
  }

  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }
}
