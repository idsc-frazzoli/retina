// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.io.IOException;

import ch.ethz.idsc.sophus.poly.Polygons;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class AxisAlignedBoxTest extends TestCase {
  public void testAlongX() throws ClassNotFoundException, IOException {
    AxisAlignedBox axisAlignedBox = Serialization.copy(new AxisAlignedBox(RealScalar.of(0.2)));
    Tensor tensor = axisAlignedBox.alongX(RealScalar.of(2.5));
    assertTrue(Polygons.isInside(tensor, Tensors.vector(2.2, 0.05)));
    assertEquals(tensor.length(), 4);
  }

  public void testAlongY() throws ClassNotFoundException, IOException {
    AxisAlignedBox axisAlignedBox = Serialization.copy(new AxisAlignedBox(RealScalar.of(0.2)));
    Tensor tensor = axisAlignedBox.alongY(RealScalar.of(2.5));
    assertTrue(Polygons.isInside(tensor, Tensors.vector(0.05, 2.2)));
    assertEquals(tensor.length(), 4);
  }
}
