// code by mheim
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.Clip;

// TODO switch the whole thing to Tensor variables (this will not change any interactions)
// TODO document this properly (to be done after the whole thing works)
public class LookupTable2D implements Serializable {
  private static final float TOLERANCE = 0.001f;
  private static final Scalar HALF = RealScalar.of(0.5);

  public static LookupTable2D build( //
      BinaryOperator<Scalar> function, //
      int firstDimN, int secondDimN, //
      Clip firstDimClip, //
      Clip secondDimClip) {
    Scalar[][] table = new Scalar[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(firstDimClip.min(), firstDimClip.max(), firstDimN - 1);
    Tensor s1 = Subdivide.of(secondDimClip.min(), secondDimClip.max(), secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0)
      for (int i1 = 0; i1 < secondDimN; ++i1)
        table[i0][i1] = function.apply(s0.Get(i0), s1.Get(i1));
    return new LookupTable2D( //
        Tensors.matrix(table), //
        firstDimClip, //
        secondDimClip);
  }

  // ---
  final Tensor tensor;
  private final Clip clip0;
  private final Clip clip1;
  // private final Unit outputUnit;
  private final Tensor scale;
  private final Interpolation interpolation;

  /* package */ LookupTable2D( //
      Tensor tensor, //
      Clip clip0, //
      Clip clip1) {
    this.tensor = tensor;
    this.clip0 = clip0;
    this.clip1 = clip1;
    // this.outputUnit = outputUnit;
    scale = Tensors.vector(tensor.length() - 1, tensor.get(0).length() - 1);
    interpolation = LinearInterpolation.of(tensor);
  }

  /** get inverted lookup table target specifies which of
   * the arguments gets to be the output
   * 
   * @param function monotone
   * @param target
   * @param firstDimN
   * @param secondDimN
   * @param newDimClip
   * @return */
  public LookupTable2D getInverseLookupTableBinarySearch( //
      BinaryOperator<Scalar> function, //
      int target, //
      int firstDimN, int secondDimN, //
      Clip newDimClip) {
    Scalar firstDimMin;
    Scalar firstDimMax;
    Scalar secondDimMin;
    Scalar secondDimMax;
    if (target == 0) {
      firstDimMin = newDimClip.min();
      firstDimMax = newDimClip.max();
      secondDimMin = clip1.min(); // secondDimMin;
      secondDimMax = clip1.max();
    } else //
    if (target == 1) {
      firstDimMin = clip0.min();
      firstDimMax = clip0.max();
      secondDimMin = newDimClip.min();
      secondDimMax = newDimClip.max();
    } else
      return null;
    // switch x and out
    Scalar[][] table = new Scalar[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(firstDimMin, firstDimMax, firstDimN - 1);
    Tensor s1 = Subdivide.of(secondDimMin, secondDimMax, secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0) {
      final Scalar value0 = s0.Get(i0);
      for (int i1 = 0; i1 < secondDimN; ++i1) {
        final Scalar value1 = s1.Get(i1);
        // find appropriate value
        // use approximative gradient descent
        Scalar mid = null;
        if (target == 0) {
          mid = clip0.min().zero();
          Scalar lower = clip0.min(); // firstDimMin;
          Scalar upper = clip0.max(); // firstDimMax;
          while (upper.subtract(lower).abs().number().floatValue() > TOLERANCE) {
            mid = lower.add(upper).multiply(HALF);
            final Scalar midValue = function.apply(mid, value1);
            if (Scalars.lessThan(value0, midValue))
              upper = mid;
            else
              lower = mid;
          }
        } else //
        if (target == 1) {
          mid = clip1.min().zero();
          Scalar lower = clip1.min(); // secondDimMin;
          Scalar upper = clip1.max(); // secondDimMax;
          while (upper.subtract(lower).abs().number().floatValue() > TOLERANCE) {
            mid = lower.add(upper).multiply(HALF);
            final Scalar midValue = function.apply(value0, mid);
            if (Scalars.lessThan(value1, midValue))
              upper = mid;
            else
              lower = mid;
          }
        }
        table[i0][i1] = mid;
      }
    }
    if (target == 0)
      return new LookupTable2D( //
          Tensors.matrix(table), //
          newDimClip, //
          clip1);
    if (target == 1)
      return new LookupTable2D(//
          Tensors.matrix(table), //
          clip0, //
          newDimClip);
    return null;
  }

  public Scalar lookup(Scalar x, Scalar y) {
    return // Quantity.of(
    interpolation.Get(Tensors.of( //
        clip0.rescale(x), //
        clip1.rescale(y)).pmul(scale));// , outputUnit);
  }

  /** delivers the extremal values in the specified direction
   * 
   * @param dimension the dimension along which the extremal are to be found
   * @param otherValue the value that is set at the other dimension
   * @return a tensor containing the minimum and maximum value along the dimension */
  public Tensor getExtremalValues(int dimension, Scalar otherValue) {
    if (dimension == 0)
      return Tensors.of( //
          lookup(clip0.min(), otherValue), //
          lookup(clip0.max(), otherValue));
    if (dimension == 1)
      return Tensors.of( //
          lookup(otherValue, clip1.min()), //
          lookup(otherValue, clip1.max()));
    return null;
  }

  /** export lookup table to csv format
   * 
   * @param bufferedWriter
   * @throws IOException */
  public void exportToMatlab(BufferedWriter bufferedWriter) throws IOException {
    // read dimensions
    int firstDimN = tensor.length();
    int secondDimN = tensor.get(0).length();
    bufferedWriter.write(firstDimN + "\n");
    bufferedWriter.write(secondDimN + "\n");
    bufferedWriter.write(clip0.min().number().floatValue() + "," + clip0.max().number().floatValue() + "\n");
    bufferedWriter.write(clip1.min().number().floatValue() + "," + clip1.max().number().floatValue() + "\n");
    // read units
    bufferedWriter.write(Units.of(clip0.min()) + "\n");
    bufferedWriter.write(Units.of(clip1.min()) + "\n");
    bufferedWriter.write(Units.of(tensor.Get(0, 0)) + "\n");
    for (int i0 = 0; i0 < firstDimN; ++i0) {
      String[] linevals = new String[secondDimN];
      for (int i1 = 0; i1 < secondDimN; ++i1)
        linevals[i1] = String.valueOf(tensor.Get(i0, i1));
      bufferedWriter.write(String.join(",", linevals) + "\n");
    }
  }
}
