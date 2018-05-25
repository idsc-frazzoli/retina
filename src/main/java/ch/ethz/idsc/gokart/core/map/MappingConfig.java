// code by ynager
package ch.ethz.idsc.gokart.core.map;

import java.io.Serializable;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for the mapping of the gokart surroundings and obstacles */
public class MappingConfig implements Serializable {
  public static final MappingConfig GLOBAL = AppResources.load(new MappingConfig());
  /***************************************************/
  /** Prior on occupancy probability of a single cell in grid */
  public Scalar P_M = DoubleScalar.of(0.5);
  /** Probability that a cell is occupied given that
   * the LIDAR has observed an obstacle in that cell. */
  public Scalar P_M_HIT = DoubleScalar.of(0.85);
  /** Probability that a cell is occupied given that
   * the LIDAR ray has passed through it and was below minObsHeight. */
  public Scalar P_M_PASS = DoubleScalar.of(0.2);
  /** Probability threshold for occupancy threshold. Grid cells
   * with occupancy probability larger than P_THRESH are classified
   * as occupied */
  public Scalar P_THRESH = DoubleScalar.of(0.5);
  /** Forgetting factor lambda in (0,1). Lambda equal to one
   * results in past and current measurements being equally important. */
  public Scalar lambda = DoubleScalar.of(1);
  /** Occupied cells are dilated with this radius before generating
   * the obstacle map */
  public Scalar obsRadius = Quantity.of(1.5, SI.METER);
  /** Cell dimension of a single grid cell in [m] */
  public Scalar cellDim = Quantity.of(0.2, SI.METER);
  /** Minimal obstacle height. Used for inverse sensor model */
  public Scalar minObsHeight = Quantity.of(0, SI.METER);

  /***************************************************/
  public double getP_M() {
    return P_M.number().doubleValue();
  }

  public double getP_M_HIT() {
    return P_M_HIT.number().doubleValue();
  }
  
  public double getP_M_PASS() {
    return P_M_PASS.number().doubleValue();
  }

  public double getP_THRESH() {
    return P_THRESH.number().doubleValue();
  }

  public double getLambda() {
    return lambda.number().doubleValue();
  }

}
