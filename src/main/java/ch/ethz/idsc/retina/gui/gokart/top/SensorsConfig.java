// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class SensorsConfig implements Serializable {
  public static final SensorsConfig GLOBAL = AppResources.load(new SensorsConfig());
  /***************************************************/
  // TODO at some point also introduce units here
  /** urg04lx is the pose of the front lidar {px, py, angle} */
  public Tensor urg04lx = Tensors.vector(1.67, 0.0, 0.005);
  public Tensor vlp16 = Tensors.vector(0.09, 0.0, -1.61);
  /** height of vlp scan from ground
   * 1.18[m] from ground to platform top
   * 0.006[m] TODO DUBENDORF width of heat sink
   * 0.0378[m] == 37.8[mm] from heat sink to height of optical center */
  public Tensor vlp16Height = Quantity.of(1.18 + 0.006 + 0.0378, "m");
  /** shift from center of VLP16 to DAVIS */
  public Tensor vlp16_davis_t = Tensors.vectorDouble(0.2, 0, 0.5);
  public Tensor vlp16_davis_w0 = Tensors.vectorDouble(1.57, 0.0, 0.0);
  public Tensor vlp16_davis_w1 = Tensors.vectorDouble(0.0, 3.0, 0.0);
}
