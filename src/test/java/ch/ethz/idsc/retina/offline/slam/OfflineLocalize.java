// code by jph
package ch.ethz.idsc.retina.offline.slam;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.gui.gokart.top.SensorsConfig;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class OfflineLocalize implements LidarRayBlockListener {
  protected static final Tensor LIDAR = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16).unmodifiable();
  // ---
  protected final TensorBuilder tensorBuilder = new TensorBuilder();
  protected Scalar time;
  public final Tensor skipped = Tensors.empty();
  protected Tensor model;

  public OfflineLocalize(Tensor model) {
    this.model = model;
  }

  public void setTime(Scalar time) {
    this.time = time;
  }

  public Tensor getTable() {
    return tensorBuilder.getTensor();
  }
}
