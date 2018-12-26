// code by mheim
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.tensor.RealScalar;
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
public class LookupTable2D {
  private static final float TOLERANCE = 0.001f;

  static interface LookupFunction extends BinaryOperator<Scalar>, Serializable {
    // ---
  }

  public static LookupTable2D from(BufferedReader csvReader) throws IOException {
    String line;
    // read dimensions
    int firstDimN = Integer.parseInt(csvReader.readLine());
    int secondDimN = Integer.parseInt(csvReader.readLine());
    float[][] table = new float[firstDimN][secondDimN];
    line = csvReader.readLine();
    // read limits
    String[] firstLimits = line.split(",");
    float firstDimMin = Float.parseFloat(firstLimits[0]);
    float firstDimMax = Float.parseFloat(firstLimits[1]);
    line = csvReader.readLine();
    String[] secondLimits = line.split(",");
    float secondDimMin = Float.parseFloat(secondLimits[0]);
    float secondDimMax = Float.parseFloat(secondLimits[1]);
    // read units
    Unit firstDimUnit = Unit.of(csvReader.readLine());
    Unit secondDimUnit = Unit.of(csvReader.readLine());
    Unit outputUnit = Unit.of(csvReader.readLine());
    for (int i1 = 0; i1 < firstDimN; ++i1) {
      line = csvReader.readLine();
      String[] linevals = line.split(",");
      for (int i2 = 0; i2 < secondDimN; ++i2)
        table[i1][i2] = Float.parseFloat(linevals[i2]);
    }
    return new LookupTable2D(table, //
        Quantity.of(firstDimMin, firstDimUnit), Quantity.of(firstDimMax, firstDimUnit), //
        Quantity.of(secondDimMin, secondDimUnit), Quantity.of(secondDimMax, secondDimUnit), //
        outputUnit);
  }

  public static LookupTable2D build( //
      LookupFunction function, //
      int firstDimN, //
      int secondDimN, //
      Scalar firstDimMin, Scalar firstDimMax, //
      Scalar secondDimMin, Scalar secondDimMax, //
      Unit outputUnit) {
    float firstDimMinf = firstDimMin.number().floatValue();
    float firstDimMaxf = firstDimMax.number().floatValue();
    float secondDimMinf = secondDimMin.number().floatValue();
    float secondDimMaxf = secondDimMax.number().floatValue();
    float[][] table = new float[firstDimN][secondDimN];
    for (int i1 = 0; i1 < firstDimN; ++i1) {
      for (int i2 = 0; i2 < secondDimN; ++i2) {
        float firstValuef = firstDimMinf + (firstDimMaxf - firstDimMinf) * i1 / (firstDimN - 1);
        float secondValuef = secondDimMinf + (secondDimMaxf - secondDimMinf) * i2 / (secondDimN - 1);
        table[i1][i2] = function.apply( //
            Quantity.of(firstValuef, Units.of(firstDimMin)), //
            Quantity.of(secondValuef, Units.of(secondDimMin))).number().floatValue();
      }
    }
    return new LookupTable2D(table, firstDimMin, firstDimMax, secondDimMin, secondDimMax, outputUnit);
  }

  // ---
  private final Clip clip0;
  private final Clip clip1;
  final float table[][];
  // ---
  private final Interpolation interpolation;
  private final Unit outputUnit;

  public void saveTable(BufferedWriter csvWriter) throws IOException {
    // read dimensions
    int firstDimN = table.length;
    int secondDimN = table[0].length;
    csvWriter.write(firstDimN + "\n");
    csvWriter.write(secondDimN + "\n");
    csvWriter.write(clip0.min().number().floatValue() + "," + clip0.max().number().floatValue() + "\n");
    csvWriter.write(clip1.min().number().floatValue() + "," + clip1.max().number().floatValue() + "\n");
    // read units
    csvWriter.write(Units.of(clip0.min()) + "\n");
    csvWriter.write(Units.of(clip1.min()) + "\n");
    csvWriter.write(outputUnit + "\n");
    for (int i1 = 0; i1 < firstDimN; i1++) {
      String[] linevals = new String[secondDimN];
      for (int i2 = 0; i2 < secondDimN; ++i2)
        linevals[i2] = String.valueOf(table[i1][i2]);
      csvWriter.write(String.join(",", linevals) + "\n");
    }
  }

  public LookupTable2D( //
      float table[][], //
      Scalar firstDimMin, Scalar firstDimMax, //
      Scalar secondDimMin, Scalar secondDimMax, //
      Unit outputUnit) {
    this.table = table;
    clip0 = Clip.function(firstDimMin, firstDimMax);
    clip1 = Clip.function(secondDimMin, secondDimMax);
    this.outputUnit = outputUnit;
    interpolation = LinearInterpolation.of(Tensors.matrixFloat(table));
  }

  /** get inverted lookup table target specifies which of the arguments gets to be
   * the the output: function should be monotone */
  private float getFunctionValue(LookupFunction function, float x, float y) {
    return function.apply( //
        Quantity.of(x, Units.of(clip0.min())), //
        Quantity.of(y, Units.of(clip1.min()))).number().floatValue();
  }

  public LookupTable2D getInverseLookupTableBinarySearch( //
      LookupFunction function, //
      int target, //
      int firstDimN, int secondDimN, //
      Scalar newDimMin, Scalar newDimMax) {
    float firstDimMinf;
    float firstDimMaxf;
    float secondDimMinf;
    float secondDimMaxf;
    if (target == 0) {
      firstDimMinf = newDimMin.number().floatValue();
      firstDimMaxf = newDimMax.number().floatValue();
      secondDimMinf = clip1.min().number().floatValue(); // secondDimMin;
      secondDimMaxf = clip1.max().number().floatValue();
    } else //
    if (target == 1) {
      firstDimMinf = clip0.min().number().floatValue();
      firstDimMaxf = clip0.max().number().floatValue();
      secondDimMinf = newDimMin.number().floatValue();
      secondDimMaxf = newDimMax.number().floatValue();
    } else
      return null;
    // switch x and out
    float table[][] = new float[firstDimN][secondDimN];
    Tensor s0 = Subdivide.of(firstDimMinf, firstDimMaxf, firstDimN - 1);
    Tensor s1 = Subdivide.of(secondDimMinf, secondDimMaxf, secondDimN - 1);
    for (int i0 = 0; i0 < firstDimN; ++i0) {
      final float firstValuef = s0.Get(i0).number().floatValue();
      for (int i1 = 0; i1 < secondDimN; ++i1) {
        float secondValuef = s1.Get(i1).number().floatValue();
        // find appropriate value
        // use approximative gradient descent
        float lower;
        float upper;
        float mid = 0;
        if (target == 0) {
          lower = clip0.min().number().floatValue(); // firstDimMin;
          upper = clip0.max().number().floatValue(); // firstDimMax;
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
          lower = clip1.min().number().floatValue(); // secondDimMin;
          upper = clip1.max().number().floatValue(); // secondDimMax;
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
          table, //
          Quantity.of(firstDimMinf, outputUnit), //
          Quantity.of(firstDimMaxf, outputUnit), //
          Quantity.of(secondDimMinf, Units.of(clip1.min())), // secondDimUnit), //
          Quantity.of(secondDimMaxf, Units.of(clip1.min())), // secondDimUnit), //
          Units.of(clip0.min()) // firstDimUnit
      );
    if (target == 1)
      return new LookupTable2D(//
          table, //
          Quantity.of(firstDimMinf, Units.of(clip0.min())), // firstDimUnit
          Quantity.of(firstDimMaxf, Units.of(clip0.min())), // firstDimUnit
          Quantity.of(secondDimMinf, outputUnit), //
          Quantity.of(secondDimMaxf, outputUnit), //
          Units.of(clip1.min()) // secondDimUnit
      );
    return null;
  }

  public Scalar lookup(Scalar x, Scalar y) {
    int firstDimN = table.length;
    int secondDimN = table[0].length;
    return Quantity.of(interpolation.Get(Tensors.of( //
        clip0.rescale(x).multiply(RealScalar.of(firstDimN - 1)), //
        clip1.rescale(y).multiply(RealScalar.of(secondDimN - 1)))), outputUnit);
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
}
