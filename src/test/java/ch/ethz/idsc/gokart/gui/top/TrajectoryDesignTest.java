// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TrajectoryDesignTest extends TestCase {
  public void testSimple() {
    Tensor tensor = TrajectoryDesign.se2CtoSe2WithUnits(Tensors.vector(2, 3, 4));
    PoseHelper.toUnitless(tensor);
    Chop._10.requireClose(tensor.get(2), RealScalar.of(-2.2831853071795862));
  }
}
