// code by mheim
package ch.ethz.idsc.gokart.calib.power;

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
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

/** implementation is not a strict lookup but uses {@link LinearInterpolation} */
public class LookupTable2D implements Serializable {
  private static final Scalar HALF = RealScalar.of(0.5);

  public static LookupTable2D build( //
      BinaryOperator<Scalar> function, //
      int firstDimN, int secondDimN, //
      Clip clip0, Clip clip1) {
    Scalar[][] table = new Scalar[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(clip0, firstDimN - 1);
    Tensor s1 = Subdivide.of(clip1, secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0)
      for (int i1 = 0; i1 < secondDimN; ++i1)
        table[i0][i1] = function.apply(s0.Get(i0), s1.Get(i1));
    return new LookupTable2D(table, clip0, clip1);
  }

  // ---
  private final Clip clip0;
  private final Clip clip1;
  /** linear interpolation */
  private final Interpolation interpolation;
  /** multiplier used in {@link #lookup(Scalar, Scalar)} */
  private final Tensor scale;
  /** output unit */
  private final Unit unit;

  /* package */ LookupTable2D(Scalar[][] table, Clip clip0, Clip clip1) {
    this.clip0 = clip0;
    this.clip1 = clip1;
    unit = QuantityUnit.of(table[0][0]);
    interpolation = LinearInterpolation.of(Tensors.matrix(table).map(QuantityMagnitude.singleton(unit)));
    scale = Tensors.vector(table.length - 1, table[0].length - 1);
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
    Clip clipNT = target == 0 ? clip0 : clip1;
    // switch x and out
    Scalar[][] table = new Scalar[dimN0][dimN1];
    Tensor s0 = Subdivide.of(clipN0, dimN0 - 1);
    Tensor s1 = Subdivide.of(clipN1, dimN1 - 1);
    for (int i0 = 0; i0 < dimN0; ++i0) {
      Scalar value0 = s0.Get(i0);
      for (int i1 = 0; i1 < dimN1; ++i1) {
        Scalar value1 = s1.Get(i1);
        // find appropriate value
        // use approximative gradient descent
        Scalar mid = null;
        Scalar lower = clipNT.min();
        Scalar upper = clipNT.max();
        Scalar value = target == 0 ? value0 : value1;
        while (!chop.close(lower, upper)) {
          mid = lower.add(upper).multiply(HALF);
          Scalar midValue = target == 0 //
              ? function.apply(mid, value1)
              : function.apply(value0, mid);
          if (Scalars.lessThan(value, midValue))
            upper = mid;
          else
            lower = mid;
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
    int dimN0 = scale.Get(0).number().intValue() + 1;
    int dimN1 = scale.Get(1).number().intValue() + 1;
    bufferedWriter.write(dimN0 + "\n");
    bufferedWriter.write(dimN1 + "\n");
    bufferedWriter.write(clip0.min().number().floatValue() + "," + clip0.max().number().floatValue() + "\n");
    bufferedWriter.write(clip1.min().number().floatValue() + "," + clip1.max().number().floatValue() + "\n");
    // write units
    bufferedWriter.write(QuantityUnit.of(clip0.min()) + "\n");
    bufferedWriter.write(QuantityUnit.of(clip1.min()) + "\n");
    bufferedWriter.write(unit + "\n");
    for (int i0 = 0; i0 < dimN0; ++i0) {
      String[] linevals = new String[dimN1];
      for (int i1 = 0; i1 < dimN1; ++i1)
        linevals[i1] = interpolation.get(Tensors.vector(i0, i1)).toString();
      bufferedWriter.write(String.join(",", linevals) + "\n");
    }
  }
}
