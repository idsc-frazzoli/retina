// code by jph
package ch.ethz.idsc.gokart.offline;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.gui.gokart.top.SensorsConfig;
import ch.ethz.idsc.retina.util.math.TableBuilder;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public abstract class OfflineLocalize implements LidarRayBlockListener {
  protected static final Tensor LIDAR = Se2Utils.toSE2Matrix(SensorsConfig.GLOBAL.vlp16).unmodifiable();
  // ---
  protected final TableBuilder tableBuilder = new TableBuilder();
  protected Scalar time;
  public final Tensor skipped = Tensors.empty();
  /** 3x3 matrix */
  protected Tensor model;

  public OfflineLocalize(Tensor model) {
    this.model = model;
  }

  public void setTime(Scalar time) {
    this.time = time;
  }

  public Tensor getPositionVector() {
    return Se2Utils.fromSE2Matrix(model);
  }

  public Tensor getTable() {
    return tableBuilder.toTable();
  }
}
