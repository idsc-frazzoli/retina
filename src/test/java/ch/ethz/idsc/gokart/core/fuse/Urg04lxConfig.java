// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class Urg04lxConfig {
  public static final Urg04lxConfig GLOBAL = AppResources.load(new Urg04lxConfig());
  // ---
  /** urg04lx is the pose of the front lidar {px, py, angle} */
  public final Tensor urg04lx = Tensors.vector(1.67, 0.0, 0.005);
}
