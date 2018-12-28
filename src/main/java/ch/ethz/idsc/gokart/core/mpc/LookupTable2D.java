// code by mheim
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

// TODO document this properly (to be done after the whole thing works)
public class LookupTable2D implements Serializable {
  private static final Scalar HALF = RealScalar.of(0.5);

  private static Scalar dropUnit(Scalar scalar) {
    return scalar instanceof Quantity //
        ? ((Quantity) scalar).value()
        : scalar;
  }

  public static LookupTable2D build( //
      BinaryOperator<Scalar> function, //
      int firstDimN, int secondDimN, //
      Clip clip0, Clip clip1) {
    Scalar[][] table = new Scalar[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(clip0.min(), clip0.max(), firstDimN - 1);
    Tensor s1 = Subdivide.of(clip1.min(), clip1.max(), secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0)
      for (int i1 = 0; i1 < secondDimN; ++i1)
        table[i0][i1] = function.apply(s0.Get(i0), s1.Get(i1));
    return new LookupTable2D(table, clip0, clip1);
  }

  // ---
  final Tensor tensor;
  private final Clip clip0;
  private final Clip clip1;
  private final Tensor scale;
  private final Interpolation interpolation;
  private final Unit unit;

  /* package */ LookupTable2D(Scalar[][] table, Clip clip0, Clip clip1) {
    unit = Units.of(table[0][0]);
    this.tensor = Tensors.matrix(table).map(LookupTable2D::dropUnit);
    this.clip0 = clip0;
    this.clip1 = clip1;
    scale = Tensors.vector(tensor.length() - 1, tensor.get(0).length() - 1);
    interpolation = LinearInterpolation.of(tensor);
  }

  /** get inverted lookup table target specifies which of
   * the arguments gets to be the output
   * 
   * @param function monotone
   * @param target
   * @param dimN0
   * @param dimN1
   * @param clipT
   * @param chop
   * @return */
  public LookupTable2D getInverseLookupTableBinarySearch( //
      BinaryOperator<Scalar> function, int target, //
      int dimN0, int dimN1, Clip clipT, Chop chop) {
    Clip clipN0 = target == 0 ? clipT : clip0;
    Clip clipN1 = target == 0 ? clip1 : clipT;
    // switch x and out
    Scalar[][] table = new Scalar[dimN0][dimN1];
    Tensor s0 = Subdivide.of(clipN0.min(), clipN0.max(), dimN0 - 1);
    Tensor s1 = Subdivide.of(clipN1.min(), clipN1.max(), dimN1 - 1);
    for (int i0 = 0; i0 < dimN0; ++i0) {
      final Scalar value0 = s0.Get(i0);
      for (int i1 = 0; i1 < dimN1; ++i1) {
        final Scalar value1 = s1.Get(i1);
        // find appropriate value
        // use approximative gradient descent
        Scalar mid = null;
        if (target == 0) {
          Scalar lower = clip0.min();
          Scalar upper = clip0.max();
          while (!chop.close(lower, upper)) {
            mid = lower.add(upper).multiply(HALF);
            final Scalar midValue = function.apply(mid, value1);
            if (Scalars.lessThan(value0, midValue))
              upper = mid;
            else
              lower = mid;
          }
        } else //
        if (target == 1) {
          Scalar lower = clip1.min();
          Scalar upper = clip1.max();
          while (!chop.close(lower, upper)) {
            mid = lower.add(upper).multiply(HALF);
            final Scalar midValue = function.apply(value0, mid);
            if (Scalars.lessThan(value1, midValue))
              upper = mid;
            else
              lower = mid;
          }
        }
        table[i0][i1] = Objects.requireNonNull(mid);
      }
    }
    return new LookupTable2D(table, clipN0, clipN1);
  }

  public Scalar lookup(Scalar x, Scalar y) {
    return Quantity.of(interpolation.Get(Tensors.of( //
        clip0.rescale(x), //
        clip1.rescale(y)).pmul(scale)), unit);
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
    // write dimensions
    int firstDimN = tensor.length();
    int secondDimN = tensor.get(0).length();
    bufferedWriter.write(firstDimN + "\n");
    bufferedWriter.write(secondDimN + "\n");
    bufferedWriter.write(clip0.min().number().floatValue() + "," + clip0.max().number().floatValue() + "\n");
    bufferedWriter.write(clip1.min().number().floatValue() + "," + clip1.max().number().floatValue() + "\n");
    // write units
    bufferedWriter.write(Units.of(clip0.min()) + "\n");
    bufferedWriter.write(Units.of(clip1.min()) + "\n");
    bufferedWriter.write(Units.of(tensor.Get(0, 0)) + "\n");
    for (int i0 = 0; i0 < firstDimN; ++i0) {
      String[] linevals = new String[secondDimN];
      for (int i1 = 0; i1 < secondDimN; ++i1)
        linevals[i1] = tensor.Get(i0, i1).toString();
      bufferedWriter.write(String.join(",", linevals) + "\n");
    }
  }
}
