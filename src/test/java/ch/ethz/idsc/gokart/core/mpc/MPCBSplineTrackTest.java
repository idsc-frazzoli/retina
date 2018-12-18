package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class MPCBSplineTrackTest extends TestCase {
  public void testSaving() {
    Tensor ctrX = Tensors.vector(0, 1, 2).multiply(Quantity.of(1, SI.METER));
    Tensor ctrY = Tensors.vector(3, 4, 5).multiply(Quantity.of(1, SI.METER));
    Tensor ctrR = Tensors.vector(6, 7, 8).multiply(Quantity.of(1, SI.METER));
    Scalar prog = RealScalar.ZERO;
    MPCPathParameter mpcPathParameter = new MPCPathParameter(prog, ctrX, ctrY, ctrR);
    byte[] bytes = new byte[1000];
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    mpcPathParameter.insert(buffer);
    buffer.rewind();
    MPCPathParameter mpcPathParameter2 = new MPCPathParameter(buffer);
    Tensor ctrX2 = mpcPathParameter2.getControlPointsX();
    Tensor ctrY2 = mpcPathParameter2.getControlPointsY();
    Tensor ctrR2 = mpcPathParameter2.getControlPointsR();
    assertTrue(ctrX.equals(ctrX2));
    assertTrue(ctrY.equals(ctrY2));
    assertTrue(ctrR.equals(ctrR2));
    assertTrue(mpcPathParameter.getProgressOnPath().equals(mpcPathParameter2.getProgressOnPath()));
  }

  public void testQuery1() {
    Tensor ctrX = Tensors.vector(0, 1, 2).multiply(Quantity.of(1, SI.METER));
    Tensor ctrY = Tensors.vector(3, 4, 5).multiply(Quantity.of(1, SI.METER));
    Tensor ctrR = Tensors.vector(6, 7, 8).multiply(Quantity.of(1, SI.METER));
    MPCBSplineTrack mpcbSplineTrack = new MPCBSplineTrack(ctrX, ctrY, ctrR, true);
    MPCPathParameter mpcPathParameter = mpcbSplineTrack.getPathParameterPreview(3, Tensors.vector(1.1, 4).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER));
    assertEquals(mpcPathParameter.controlPointsX, Tensors.vector(0, 1, 2).multiply(Quantity.of(1, SI.METER)));
    assertEquals(mpcPathParameter.controlPointsY, Tensors.vector(3, 4, 5).multiply(Quantity.of(1, SI.METER)));
    assertEquals(mpcPathParameter.controlPointsR, Tensors.vector(6, 7, 8).multiply(Quantity.of(1, SI.METER)));
  }

  public void testQuery2() {
    Tensor ctrX = Tensors.vector(0, 1, 2).multiply(Quantity.of(1, SI.METER));
    Tensor ctrY = Tensors.vector(3, 4, 5).multiply(Quantity.of(1, SI.METER));
    Tensor ctrR = Tensors.vector(6, 7, 8).multiply(Quantity.of(1, SI.METER));
    MPCBSplineTrack mpcbSplineTrack = new MPCBSplineTrack(ctrX, ctrY, ctrR, true);
    long startTime = System.nanoTime();
    MPCPathParameter mpcPathParameter = mpcbSplineTrack.getPathParameterPreview(5, Tensors.vector(0, 3).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER));
    long endTime = System.nanoTime();
    System.out.println(" path progress timing: " + (endTime - startTime) + "[ns]");
    assertEquals(mpcPathParameter.controlPointsX, Tensors.vector(2, 0, 1, 2, 0).multiply(Quantity.of(1, SI.METER)));
    assertEquals(mpcPathParameter.controlPointsY, Tensors.vector(5, 3, 4, 5, 3).multiply(Quantity.of(1, SI.METER)));
    assertEquals(mpcPathParameter.controlPointsR, Tensors.vector(8, 6, 7, 8, 6).multiply(Quantity.of(1, SI.METER)));
  }

  public void testQuery3() {
    Tensor ctrX = Tensors.vector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10).multiply(Quantity.of(1, SI.METER));
    Tensor ctrY = Tensors.vector(3, 4, 5, 3, 4, 5, 6, 7, 8, 9, 10).multiply(Quantity.of(1, SI.METER));
    Tensor ctrR = Tensors.vector(6, 7, 8, 3, 4, 5, 6, 7, 8, 9, 10).multiply(Quantity.of(1, SI.METER));
    MPCBSplineTrack mpcbSplineTrack = new MPCBSplineTrack(ctrX, ctrY, ctrR, true);
    long startTime = System.nanoTime();
    MPCPathParameter mpcPathParameter = mpcbSplineTrack.getPathParameterPreview(5, Tensors.vector(0, 3).multiply(Quantity.of(1, SI.METER)),
        Quantity.of(0, SI.METER));
    long endTime = System.nanoTime();
    System.out.println(" path progress timing: " + (endTime - startTime) + "[ns]");
  }
}
