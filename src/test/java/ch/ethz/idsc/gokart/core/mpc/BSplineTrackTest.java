// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Random;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BSplineTrackTest extends TestCase {
  public void testFunction() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor xtensor = Tensors.vector(0, 1, 2, 3, 4, 5).multiply(meter);
    Tensor ytensor = Tensors.vector(0, 1, 0, 1, 0, 1).multiply(meter);
    Tensor rtensor = Tensors.vector(2, 2, 2, 2, 2, 2).multiply(meter);
    BSplineTrack bSplineTrack = new BSplineTrack(xtensor, ytensor, rtensor);
    Tensor out = Tensors.empty();
    Tensor devout = Tensors.empty();
    Tensor devdevout = Tensors.empty();
    for (int i = 0; i < 100; i++) {
      double x = i / 10.;
      // System.out.println(x+": "+bSplineTrack.getPosition(Quantity.of(x, SI.ONE)));
      // System.out.println(x+" dev: "+bSplineTrack.getDerivation(Quantity.of(x, SI.ONE)));
      out.append(bSplineTrack.getPosition(Quantity.of(x, SI.ONE)));
      devout.append(bSplineTrack.getDerivation(Quantity.of(x, SI.ONE)));
      devdevout.append(bSplineTrack.get2ndDerivation(Quantity.of(x, SI.ONE)));
    }
    // System.out.println(out);
    // looks good!
    /* File file = new File("bsplineOut.csv");
     * try {
     * Export.of(file, out.divide(meter));
     * } catch (IOException e) {
     * // TODO Auto-generated catch block
     * e.printStackTrace();
     * }
     * File dfile = new File("bsplinedOut.csv");
     * try {
     * Export.of(dfile, devout.divide(meter));
     * } catch (IOException e) {
     * // TODO Auto-generated catch block
     * e.printStackTrace();
     * }
     * File ddfile = new File("bsplineddOut.csv");
     * try {
     * Export.of(ddfile, devdevout.divide(meter));
     * } catch (IOException e) {
     * // TODO Auto-generated catch block
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
    BSplineTrack bSplineTrack = new BSplineTrack(xtensor, ytensor, rtensor);
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      Scalar prog = Quantity.of(rand.nextFloat() * 200 - 100, SI.ONE);
      // get from bspline track
      Tensor cDev = bSplineTrack.getDerivation(prog);
      // compute numerically
      Scalar dx = Quantity.of(0.00001, SI.ONE);
      Tensor nDev = bSplineTrack.getPosition(prog.add(dx))//
          .subtract(bSplineTrack.getPosition(prog))//
          .divide(dx);
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
    BSplineTrack bSplineTrack = new BSplineTrack(xtensor, ytensor, rtensor);
    Random rand = new Random();
    for (int i = 0; i < 100; i++) {
      Scalar prog = Quantity.of(rand.nextFloat() * 200 - 100, SI.ONE);
      // get from bspline track
      Tensor cDDev = bSplineTrack.get2ndDerivation(prog);
      // compute numerically
      Scalar dx = Quantity.of(0.00001, SI.ONE);
      Tensor nDevp = bSplineTrack.getPosition(prog.add(dx))//
          .subtract(bSplineTrack.getPosition(prog))//
          .divide(dx);
      Tensor nDev = bSplineTrack.getPosition(prog)//
          .subtract(bSplineTrack.getPosition(prog.subtract(dx)))//
          .divide(dx);
      Tensor nDDev = nDevp.subtract(nDev).divide(dx);
      // System.out.println("2nd der: "+cDDev+" numerically: "+nDDev);
      assertTrue(Chop._04.close(cDDev, nDDev));
    }
  }

  public void testNearestPos() {
    // Tensor tensor = Tensors.of(Quantity.of(0, SI.METER),Quantity.of(1, SI.METER));
    Scalar meter = Quantity.of(1, SI.METER);
    Tensor ctrX = Tensors.of(RealScalar.ZERO, RealScalar.ZERO, RealScalar.ONE, RealScalar.ONE).multiply(meter);
    Tensor ctrY = Tensors.of(RealScalar.ZERO, RealScalar.ONE, RealScalar.ONE, RealScalar.ZERO).multiply(meter);
    Tensor ctrR = Tensors.of(RealScalar.ONE, RealScalar.ONE, RealScalar.ONE, RealScalar.ONE).multiply(meter);
    BSplineTrack bSplineTrack = new BSplineTrack(ctrX, ctrY, ctrR);
    Random rand = new Random();
    for (int i = 0; i < 10; i++) {
      Tensor queryPos = Tensors.of(Quantity.of(rand.nextFloat() * 3, SI.METER), Quantity.of(rand.nextFloat() * 4, SI.METER));
      Tensor nearestPos = bSplineTrack.getNearestPosition(queryPos);
      Scalar dist = Norm._2.of(nearestPos.subtract(queryPos));
      // check if we can find any position that is nearer
      for (int itest = 0; itest < 400; itest++) {
        Scalar testProg = Quantity.of(itest / 100.0, SI.ONE);
        Scalar testdist = Norm._2.of(bSplineTrack.getPosition(testProg).subtract(queryPos));
        // System.out.println("dist: "+dist+" test: "+testdist);
        // we can make it more precise but it costs time
        assertTrue(Scalars.lessThan(dist, testdist.add(Quantity.of(0.01, SI.METER))));
      }
    }
  }
}
