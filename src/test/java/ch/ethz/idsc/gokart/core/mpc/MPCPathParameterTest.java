// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import junit.framework.TestCase;

public class MPCPathParameterTest extends TestCase {
  public void testSaving() {
    Tensor ctrX = QuantityTensor.of(Tensors.vector(0, 1, 2), SI.METER);
    Tensor ctrY = QuantityTensor.of(Tensors.vector(3, 4, 5), SI.METER);
    Tensor ctrR = QuantityTensor.of(Tensors.vector(6, 7, 8), SI.METER);
    Scalar prog = RealScalar.ZERO;
    MPCPathParameter mpcPathParameter = new MPCPathParameter(prog, Transpose.of(Tensors.of(ctrX, ctrY, ctrR)));
    byte[] bytes = new byte[1000];
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    mpcPathParameter.insert(buffer);
    buffer.rewind();
    MPCPathParameter mpcPathParameter2 = new MPCPathParameter(buffer);
    Tensor ctrX2 = mpcPathParameter2.getControlPointsX();
    Tensor ctrY2 = mpcPathParameter2.getControlPointsY();
    Tensor ctrR2 = mpcPathParameter2.getControlPointsR();
    assertEquals(ctrX, ctrX2);
    assertEquals(ctrY, ctrY2);
    assertEquals(ctrR, ctrR2);
    assertTrue(mpcPathParameter.getProgressOnPath().equals(mpcPathParameter2.getProgressOnPath()));
  }

  public void testQuery1() {
    Tensor ctrX = QuantityTensor.of(Tensors.vector(0, 1, 2, 6, 2, 10), SI.METER);
    Tensor ctrY = QuantityTensor.of(Tensors.vector(3, 4, 5, 7, 8, 9), SI.METER);
    Tensor ctrR = QuantityTensor.of(Tensors.vector(6, 7, 8, 1, 2, 3), SI.METER);
    MPCBSplineTrack mpcbSplineTrack = new MPCBSplineTrack(Transpose.of(Tensors.of(ctrX, ctrY, ctrR)), true);
    MPCPathParameter mpcPathParameter = mpcbSplineTrack.getPathParameterPreview(6, Tensors.vector(1.1, 4.1).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER));
    // mpcPathParameter
    assertEquals(mpcPathParameter.getControlPointsX(), ctrX);
    assertEquals(mpcPathParameter.getControlPointsY(), ctrY);
    assertEquals(mpcPathParameter.getControlPointsR(), ctrR);
  }

  public void testQP() {
    Tensor ctrX = QuantityTensor.of(Tensors.vector(0, 1, 2, 6, 2, 10), SI.METER);
    Tensor ctrY = QuantityTensor.of(Tensors.vector(3, 4, 5, 7, 8, 9), SI.METER);
    Tensor ctrR = QuantityTensor.of(Tensors.vector(6, 7, 8, 1, 2, 3), SI.METER);
    MPCBSplineTrack mpcbSplineTrack = new MPCBSplineTrack(Transpose.of(Tensors.of(ctrX, ctrY, ctrR)), true);
    MPCPathParameter mpcPathParameter0 = mpcbSplineTrack.getPathParameterPreview(6, Tensors.vector(1, 4).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER), RealScalar.of(0.1));
    MPCPathParameter mpcPathParameter1 = mpcbSplineTrack.getPathParameterPreview(6, Tensors.vector(1.1, 4.1).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER), RealScalar.of(0.1));
    MPCPathParameter mpcPathParameter2 = mpcbSplineTrack.getPathParameterPreview(6, Tensors.vector(1.2, 4.2).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER), RealScalar.of(0.1));
    // mpcPathParameter
    Tensor R0 = mpcPathParameter0.getControlPointsR();
    System.out.println(mpcPathParameter0.getProgressOnPath());
    System.out.println(R0);
    Tensor R1 = mpcPathParameter1.getControlPointsR();
    System.out.println(mpcPathParameter1.getProgressOnPath());
    Tensor R2 = mpcPathParameter2.getControlPointsR();
    System.out.println(R1);
    System.out.println(mpcPathParameter2.getProgressOnPath());
    System.out.println(R2);
    for (int i = 0; i < 6; i++) {
      // ensure that track widens with progress
      assertTrue(Scalars.lessThan(R0.Get(i), R1.Get(i)));
      assertTrue(Scalars.lessThan(R1.Get(i), R2.Get(i)));
    }
  }

  public void testQuery2() {
    Tensor ctrX = QuantityTensor.of(Tensors.vector(0, 1, 2), SI.METER);
    Tensor ctrY = QuantityTensor.of(Tensors.vector(3, 4, 5), SI.METER);
    Tensor ctrR = QuantityTensor.of(Tensors.vector(6, 7, 8), SI.METER);
    MPCBSplineTrack mpcbSplineTrack = new MPCBSplineTrack(Transpose.of(Tensors.of(ctrX, ctrY, ctrR)), true);
    long startTime = System.nanoTime();
    MPCPathParameter mpcPathParameter = //
        mpcbSplineTrack.getPathParameterPreview(5, Tensors.vector(0, 3).multiply(Quantity.of(1, SI.METER)), Quantity.of(0, SI.METER));
    long endTime = System.nanoTime();
    assertTrue(endTime - startTime < 1000_000);
    // System.out.println(" path progress timing: " + (endTime - startTime) + "[ns]");
    assertEquals(mpcPathParameter.getControlPointsX(), QuantityTensor.of(Tensors.vector(2, 0, 1, 2, 0), SI.METER));
    assertEquals(mpcPathParameter.getControlPointsY(), QuantityTensor.of(Tensors.vector(5, 3, 4, 5, 3), SI.METER));
    assertEquals(mpcPathParameter.getControlPointsR(), QuantityTensor.of(Tensors.vector(8, 6, 7, 8, 6), SI.METER));
  }
}
