// code by mheim
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.Clip;

// TODO switch the whole thing to Tensor variables (this will not change any interactions)
// TODO document this properly (to be done after the whole thing works)
public class LookupTable2D implements Serializable {
  private static final float TOLERANCE = 0.001f;

  public static LookupTable2D build( //
      BinaryOperator<Scalar> function, //
      int firstDimN, int secondDimN, //
      Clip firstDimClip, //
      Clip secondDimClip, //
      Unit outputUnit) {
    float[][] table = new float[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(firstDimClip.min(), firstDimClip.max(), firstDimN - 1);
    Tensor s1 = Subdivide.of(secondDimClip.min(), secondDimClip.max(), secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0)
      for (int i1 = 0; i1 < secondDimN; ++i1)
        table[i0][i1] = function.apply(s0.Get(i0), s1.Get(i1)).number().floatValue();
    return new LookupTable2D( //
        Tensors.matrixFloat(table), //
        firstDimClip, //
        secondDimClip, //
        outputUnit);
  }

  // ---
  final Tensor tensor;
  private final Clip clip0;
  private final Clip clip1;
  private final Unit outputUnit;
  private final Tensor scale;
  private final Interpolation interpolation;

  /* package */ LookupTable2D( //
      Tensor tensor, //
      Clip clip0, //
      Clip clip1, //
      Unit outputUnit) {
    this.tensor = tensor;
    this.clip0 = clip0;
    this.clip1 = clip1;
    this.outputUnit = outputUnit;
    scale = Tensors.vector(tensor.length() - 1, tensor.get(0).length() - 1);
    interpolation = LinearInterpolation.of(tensor);
  }

  /** get inverted lookup table target specifies which of the arguments gets to be
   * the the output: function should be monotone */
  private float getFunctionValue(BinaryOperator<Scalar> function, float x, float y) {
    return function.apply( //
        Quantity.of(x, Units.of(clip0.min())), //
        Quantity.of(y, Units.of(clip1.min()))).number().floatValue();
  }

  public LookupTable2D getInverseLookupTableBinarySearch( //
      BinaryOperator<Scalar> function, //
      int target, //
      int firstDimN, int secondDimN, //
      Clip newDimClip) {
    float firstDimMinf;
    float firstDimMaxf;
    float secondDimMinf;
    float secondDimMaxf;
    if (target == 0) {
      firstDimMinf = newDimClip.min().number().floatValue();
      firstDimMaxf = newDimClip.max().number().floatValue();
      secondDimMinf = clip1.min().number().floatValue(); // secondDimMin;
      secondDimMaxf = clip1.max().number().floatValue();
    } else //
    if (target == 1) {
      firstDimMinf = clip0.min().number().floatValue();
      firstDimMaxf = clip0.max().number().floatValue();
      secondDimMinf = newDimClip.min().number().floatValue();
      secondDimMaxf = newDimClip.max().number().floatValue();
    } else
      return null;
    // switch x and out
    float[][] table = new float[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(firstDimMinf, firstDimMaxf, firstDimN - 1);
    Tensor s1 = Subdivide.of(secondDimMinf, secondDimMaxf, secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0) {
      final float firstValuef = s0.Get(i0).number().floatValue();
      for (int i1 = 0; i1 < secondDimN; ++i1) {
        final float secondValuef = s1.Get(i1).number().floatValue();
        // find appropriate value
        // use approximative gradient descent
        float mid = 0;
        if (target == 0) {
          float lower = clip0.min().number().floatValue(); // firstDimMin;
          float upper = clip0.max().number().floatValue(); // firstDimMax;
          while (Math.abs(upper - lower) > TOLERANCE) {
            mid = (lower + upper) * 0.5f;
            final float midValue = getFunctionValue(function, mid, secondValuef);
            if (midValue > firstValuef)
              upper = mid;
            else
              lower = mid;
          }
        } else //
        if (target == 1) {
          float lower = clip1.min().number().floatValue(); // secondDimMin;
          float upper = clip1.max().number().floatValue(); // secondDimMax;
          while (Math.abs(upper - lower) > TOLERANCE) {
            mid = (lower + upper) * 0.5f;
            final float midValue = getFunctionValue(function, firstValuef, mid);
            if (midValue > secondValuef)
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
          Tensors.matrixFloat(table), //
          Clip.function( //
              Quantity.of(firstDimMinf, outputUnit), //
              Quantity.of(firstDimMaxf, outputUnit)), //
          Clip.function( //
              Quantity.of(secondDimMinf, Units.of(clip1.min())), //
              Quantity.of(secondDimMaxf, Units.of(clip1.min()))), //
          Units.of(clip0.min()));
    if (target == 1)
      return new LookupTable2D(//
          Tensors.matrixFloat(table), //
          Clip.function( //
              Quantity.of(firstDimMinf, Units.of(clip0.min())), //
              Quantity.of(firstDimMaxf, Units.of(clip0.min()))), //
          Clip.function( //
              Quantity.of(secondDimMinf, outputUnit), //
              Quantity.of(secondDimMaxf, outputUnit)), //
          Units.of(clip1.min()));
    return null;
  }

  public Scalar lookup(Scalar x, Scalar y) {
    return Quantity.of(interpolation.Get(Tensors.of( //
        clip0.rescale(x), //
        clip1.rescale(y)).pmul(scale)), outputUnit);
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
    bufferedWriter.write(outputUnit + "\n");
    for (int i0 = 0; i0 < firstDimN; ++i0) {
      String[] linevals = new String[secondDimN];
      for (int i1 = 0; i1 < secondDimN; ++i1)
        linevals[i1] = String.valueOf(tensor.Get(i0, i1));
      bufferedWriter.write(String.join(",", linevals) + "\n");
    }
  }
}
