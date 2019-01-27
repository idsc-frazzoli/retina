package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;
import junit.framework.TestCase;

public class SimpleVelocityEstimationTest extends TestCase {
  public void testSimple() {
    VelocityEstimationConfig.GLOBAL.correctionFactor = Quantity.of(0.9, SI.ONE);
    Scalar accUnit = Quantity.of(1, SI.ACCELERATION);
    Scalar deltaT = Quantity.of(0.01, SI.SECOND);
    Scalar deltaTl = Quantity.of(0.1, SI.SECOND);
    Distribution distr = NormalDistribution.of(0, 10);
    SimpleVelocityEstimation estimation = new SimpleVelocityEstimation();
    Tensor originPos = Tensors.of(//
        Quantity.of(0, SI.METER), Quantity.of(0, SI.METER), Quantity.of(0, SI.ONE));
    Scalar rotVelocity = Quantity.of(0, SI.PER_SECOND);
    for (int i = 0; i < 1000; i++) {
      for (int ii = 0; ii < 10; ii++) {
        Scalar accX = RandomVariate.of(distr).multiply(accUnit);
        Scalar accY = RandomVariate.of(distr).multiply(accUnit);
        estimation.measureAcceleration(Tensors.of(accX, accY), rotVelocity, deltaT);
      }
      estimation.measurePose(originPos, deltaTl);
    }
    System.out.println(estimation.velocity);
    assertTrue(Scalars.lessThan(Norm._2.of(estimation.velocity), Quantity.of(0.1, SI.VELOCITY)));
  }
}
