// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.pdf.ContinuousDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.EmpiricalDistribution;
import ch.ethz.idsc.tensor.pdf.Expectation;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.pdf.MeanInterface;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.pdf.VarianceInterface;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Increment;

// TODO JPH TENSOR 075 obsolete
public class EqualizingDistribution implements //
    ContinuousDistribution, InverseCDF, MeanInterface, VarianceInterface, Serializable {
  public static Distribution fromUnscaledPDF(Tensor unscaledPDF) {
    return new EqualizingDistribution(unscaledPDF);
  }

  // ---
  private final EmpiricalDistribution empiricalDistribution;

  private EqualizingDistribution(Tensor unscaledPDF) {
    empiricalDistribution = (EmpiricalDistribution) EmpiricalDistribution.fromUnscaledPDF(unscaledPDF);
  }

  @Override // from PDF
  public Scalar at(Scalar x) {
    return empiricalDistribution.at(Floor.FUNCTION.apply(x));
  }

  @Override // from MeanInterface
  public Scalar mean() {
    return empiricalDistribution.mean().add(RationalScalar.HALF);
  }

  @Override // from CDF
  public Scalar p_lessThan(Scalar x) {
    Scalar xlo = Floor.FUNCTION.apply(x);
    Scalar ofs = Clips.interval(xlo, Increment.ONE.apply(xlo)).rescale(x);
    return LinearInterpolation.of(Tensors.of( //
        empiricalDistribution.p_lessThan(xlo), //
        empiricalDistribution.p_lessEquals(xlo))).At(ofs);
  }

  @Override // from CDF
  public Scalar p_lessEquals(Scalar x) {
    return p_lessThan(x);
  }

  @Override // from InverseCDF
  public Scalar quantile(Scalar p) {
    Scalar x_floor = empiricalDistribution.quantile(p);
    return x_floor.add(Clips.interval( //
        empiricalDistribution.p_lessThan(x_floor), //
        empiricalDistribution.p_lessEquals(x_floor)).rescale(p));
  }

  @Override // from RandomVariateInterface
  public Scalar randomVariate(Random random) {
    return empiricalDistribution.randomVariate(random) //
        .add(RandomVariate.of(UniformDistribution.unit(), random));
  }

  @Override // from VarianceInterface
  public Scalar variance() {
    return Expectation.variance(empiricalDistribution).add(RationalScalar.of(1, 12));
  }
}
