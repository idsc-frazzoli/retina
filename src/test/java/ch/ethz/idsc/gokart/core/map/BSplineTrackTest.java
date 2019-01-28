// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.util.Random;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.planar.Det2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class BSplineTrackTest extends TestCase {
  public void testDet2D() {
    Tensor firstDer = Tensors.vector(1, 2);
    Tensor secondDer = Tensors.vector(0, 5);
    Scalar upper = firstDer.Get(0).multiply(secondDer.Get(1)) //
        .subtract(firstDer.Get(1).multiply(secondDer.Get(0)));
    Scalar det2d = Det2D.of(firstDer, secondDer);
    Sign.requirePositive(det2d);
    assertEquals(upper, det2d);
  }

  public void testLookupRes() {
    assertEquals(BSplineTrack.LOOKUP_RES, 0.005f);
  }

  public void testFunction() {
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor fullTensor = Tensors.matrix(new Number[][] { //
        { 0, 0, 2 }, //
        { 1, 1, 2 }, //
        { 2, 0, 2 }, //
        { 3, 1, 2 }, //
        { 4, 0, 2 }, //
        { 5, 1, 2 } }).multiply(meter);
    BSplineTrack bSplineTrack = new BSplineTrack(fullTensor, true);
    assertEquals(bSplineTrack.getPositionXY(RealScalar.of(0)), Tensors.fromString("{0.5[m], 0.5[m]}"));
    assertEquals(bSplineTrack.getPositionXY(RealScalar.of(1)), Tensors.fromString("{1.5[m], 0.5[m]}"));
    assertEquals(bSplineTrack.getPositionXY(RealScalar.of(2)), Tensors.fromString("{2.5[m], 0.5[m]}"));
    {
      Tensor rightDirection = bSplineTrack.getLeftDirectionXY(RealScalar.of(0.3));
      Tensor vector = Tensors.vector(-0.37139067635410367, 0.9284766908852594);
      Chop._12.requireClose(rightDirection, vector);
    }
    Tensor out = Tensors.empty();
    Tensor devout = Tensors.empty();
    Tensor devdevout = Tensors.empty();
    for (int i = 0; i < 100; i++) {
      double x = i / 10.;
      // System.out.println(x + ": " + bSplineTrack.getPosition(Quantity.of(x, SI.ONE)));
      // System.out.println(x + " dev: " + bSplineTrack.getDerivation(Quantity.of(x, SI.ONE)));
      out.append(bSplineTrack.getPositionXY(Quantity.of(x, SI.ONE)));
      devout.append(bSplineTrack.getDerivationXY(Quantity.of(x, SI.ONE)));
      devdevout.append(bSplineTrack.get2ndDerivation(Quantity.of(x, SI.ONE)));
    }
    // System.out.println(out);
    // looks good!
    /* File file = new File("bsplineOut.csv");
     * try {
     * Export.of(file, out.divide(meter));
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * File dfile = new File("bsplinedOut.csv");
     * try {
     * Export.of(dfile, devout.divide(meter));
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * File ddfile = new File("bsplineddOut.csv");
     * try {
     * Export.of(ddfile, devdevout.divide(meter));
     * } catch (IOException e) {
     * e.printStackTrace();
     * } */
  }

  public void testDerivation() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Distribution distribution = NormalDistribution.of(0, 1);
    int N = 5;
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor xtensor = RandomVariate.of(distribution, N).multiply(meter);
    Tensor ytensor = RandomVariate.of(distribution, N).multiply(meter);
    Tensor rtensor = RandomVariate.of(distribution, N).multiply(meter);
    Tensor fullTensor = Transpose.of(Tensors.of(xtensor, ytensor, rtensor));
    BSplineTrack bSplineTrack = new BSplineTrack(fullTensor, true);
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      Scalar prog = RealScalar.of(rand.nextFloat() * 200 - 100);
      // get from bspline track
      Tensor cDev = bSplineTrack.getDerivationXY(prog);
      // compute numerically
      Scalar dx = RealScalar.of(0.00001);
      Tensor nDev = bSplineTrack.getPositionXY(prog.add(dx))//
          .subtract(bSplineTrack.getPositionXY(prog))//
          .divide(dx);
      // System.out.println(cDev.subtract(nDev));
      Scalar scalar = Norm._2.between(cDev, nDev);
      // System.out.println(scalar);
      // System.out.println(cDev.subtract(nDev));
      assertTrue(Chop._04.close(cDev, nDev));
    }
  }

  public void test2ndDerivation() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Distribution distribution = NormalDistribution.of(0, 1);
    int N = 5;
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor xtensor = RandomVariate.of(distribution, N).multiply(meter);
    Tensor ytensor = RandomVariate.of(distribution, N).multiply(meter);
    Tensor rtensor = RandomVariate.of(distribution, N).multiply(meter);
    Tensor fullTensor = Transpose.of(Tensors.of(xtensor, ytensor, rtensor));
    BSplineTrack bSplineTrack = new BSplineTrack(fullTensor, true);
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      Scalar prog = Quantity.of(rand.nextFloat() * 200 - 100, SI.ONE);
      // get from bspline track
      Tensor cDDev = bSplineTrack.get2ndDerivation(prog);
      // compute numerically
      Scalar dx = Quantity.of(0.00001, SI.ONE);
      Tensor nDevp = bSplineTrack.getPositionXY(prog.add(dx))//
          .subtract(bSplineTrack.getPositionXY(prog))//
          .divide(dx);
      Tensor nDev = bSplineTrack.getPositionXY(prog)//
          .subtract(bSplineTrack.getPositionXY(prog.subtract(dx)))//
          .divide(dx);
      Tensor nDDev = nDevp.subtract(nDev).divide(dx);
      // System.out.println("2nd der: "+cDDev+" numerically: "+nDDev);
      assertTrue(Chop._04.close(cDDev, nDDev));
    }
  }

  public void testNearestPos() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor ctrX = Tensors.vector(0, 0, 1, 1).multiply(meter);
    Tensor ctrY = Tensors.vector(0, 1, 1, 0).multiply(meter);
    Tensor ctrR = Tensors.vector(1, 1, 1, 1).multiply(meter);
    Tensor fullTensor = Transpose.of(Tensors.of(ctrX, ctrY, ctrR));
    BSplineTrack bSplineTrack = new BSplineTrack(fullTensor, true);
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      Tensor queryPos = Tensors.of(Quantity.of(rand.nextFloat() * 3, SI.METER), Quantity.of(rand.nextFloat() * 4, SI.METER));
      Tensor nearestPos = bSplineTrack.getNearestPosition(queryPos);
      Scalar dist = Norm._2.of(nearestPos.subtract(queryPos));
      // check if we can find any position that is nearer
      for (int itest = 0; itest < 400; itest++) {
        Scalar testProg = Quantity.of(itest / 100.0, SI.ONE);
        Scalar testdist = Norm._2.of(bSplineTrack.getPositionXY(testProg).subtract(queryPos));
        // System.out.println("dist: "+dist+" test: "+testdist);
        // we can make it more precise but it costs time
        assertTrue(Scalars.lessThan(dist, testdist.add(Quantity.of(0.01, SI.METER))));
      }
    }
  }

  public void testRadius() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor ctrX = Tensors.vector(0, 0, 1, 1).multiply(meter);
    Tensor ctrY = Tensors.vector(0, 1, 1, 0).multiply(meter);
    Tensor ctrR = Tensors.vector(1, 1, 1, 1).multiply(meter);
    Tensor fullTensor = Transpose.of(Tensors.of(ctrX, ctrY, ctrR));
    BSplineTrack bSplineTrack = new BSplineTrack(fullTensor, true);
    Random rand = new Random();
    bSplineTrack.getRadius(RealScalar.of(rand.nextDouble() * 100));
  }

  public void testNoOffset() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor ctrX = Tensors.vector(0, 0, 1, 1).multiply(meter);
    Tensor ctrY = Tensors.vector(0, 1, 1, 0).multiply(meter);
    Tensor ctrR = Tensors.vector(1, 1, 1, 1).multiply(meter);
    Tensor fullTensor = Transpose.of(Tensors.of(ctrX, ctrY, ctrR));
    BSplineTrack bSplineTrack = new BSplineTrack(fullTensor, true);
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      Tensor queryPos = Tensors.vector(0, 0.5).multiply(meter);
      Tensor nearestProg = bSplineTrack.getNearestPathProgress(queryPos);
      assertTrue(Chop._10.close(RealScalar.ZERO, nearestProg));
    }
  }
}
