// code by jph
package ch.ethz.idsc.retina.util.math;

import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class ParametricResampleTest extends TestCase {
  public void testSimple() {
    Scalar threshold = RealScalar.of(33);
    Scalar ds = RealScalar.of(.3);
    Tensor points = Tensors.fromString("{{100,0},{100,2},{100,3},{10,10},{10,10.2},{10,10.4},{20,40}}");
    UniformResample uniformResample = new UniformResample(threshold, ds);
    ParametricResample parametricResample = new ParametricResample(threshold, ds);
    ResampleResult resampleResult = parametricResample.apply(points);
    List<Tensor> list = uniformResample.apply(points);
    List<Tensor> pnts = resampleResult.getPoints();
    assertEquals(list.size(), pnts.size());
    for (int index = 0; index < list.size(); ++index)
      assertTrue(Chop._10.close(list.get(index), pnts.get(index)));
  }

  public void testDistances() {
    Scalar threshold = RealScalar.of(33);
    Scalar ds = RealScalar.of(.3);
    ParametricResample parametricResample = new ParametricResample(threshold, ds);
    Tensor points = CirclePoints.of(200).multiply(RealScalar.of(1));
    ResampleResult resampleResult = parametricResample.apply(points);
    List<Tensor> pnts = resampleResult.getPoints();
    assertEquals(pnts.size(), 1);
    Tensor difs = Differences.of(pnts.get(0));
    Tensor norm = Tensor.of(difs.stream().map(Norm._2::ofVector));
    Clip clip = Clip.function(0.297, 0.299);
    norm.stream().map(Scalar.class::cast).forEach(clip::requireInside);
  }

  public void testMore() {
    Scalar threshold = RealScalar.of(33);
    Scalar ds = RealScalar.of(.3);
    Tensor points = Tensors.fromString("{{10,-100},{100,0},{100,0.1},{100,0.2},{100,2},{100,2},{100,2},{100,3},{100,7},{10,10}}");
    UniformResample uniformResample = new UniformResample(threshold, ds);
    ParametricResample parametricResample = new ParametricResample(threshold, ds);
    ResampleResult resampleResult = parametricResample.apply(points);
    List<Tensor> list = uniformResample.apply(points);
    List<Tensor> pnts = resampleResult.getPoints();
    assertEquals(list.size(), pnts.size());
    for (int index = 0; index < list.size(); ++index)
      assertTrue(Chop._10.close(list.get(index), pnts.get(index)));
    assertEquals(pnts.size(), 1);
    Tensor seq = pnts.get(0);
    Tensor ys = Tensor.of(seq.stream().map(r -> r.Get(1)));
    Tensor cs = Range.of(0, seq.length()).multiply(ds);
    assertTrue(Chop._10.close(ys, cs));
  }
}
