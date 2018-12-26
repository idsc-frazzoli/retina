// code by mheim
package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import ch.ethz.idsc.tensor.sca.Clip;

//TODO: switch the whole thing to Tensor variables (this will not change any interactions)
//TODO: document this properly (to be done after the whole thing works)
public class LookupTable2D {
  private static final float TOLERANCE = 0.001f;

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
        firstDimMin, firstDimMax, //
        secondDimMin, secondDimMax, //
        firstDimUnit, secondDimUnit, //
        outputUnit);
  }

  // ---
  final float table[][];
  private final float firstDimMin;
  private final float firstDimMax;
  private final Unit firstDimUnit;
  private final Clip clip0;
  // ---
  private final float secondDimMin;
  private final float secondDimMax;
  private final Unit secondDimUnit;
  private final Clip clip1;
  // ---
  private final Interpolation interpolation;
  // ---
  private final Unit outputUnit;
  private LookupFunction originalFunction = null;

  public void saveTable(BufferedWriter csvWriter) throws IOException {
    // read dimensions
    int firstDimN = table.length;
    int secondDimN = table[0].length;
    csvWriter.write(firstDimN + "\n");
    csvWriter.write(secondDimN + "\n");
    csvWriter.write(firstDimMin + "," + firstDimMax + "\n");
    csvWriter.write(secondDimMin + "," + secondDimMax + "\n");
    // read units
    csvWriter.write(firstDimUnit + "\n");
    csvWriter.write(secondDimUnit + "\n");
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
      float firstDimMin, float firstDimMax, //
      float secondDimMin, float secondDimMax, //
      Unit firstDimUnit, Unit secondDimUnit, Unit outputUnit) {
    this.table = table;
    this.firstDimMin = firstDimMin;
    this.firstDimMax = firstDimMax;
    this.secondDimMin = secondDimMin;
    this.secondDimMax = secondDimMax;
    this.firstDimUnit = firstDimUnit;
    clip0 = Clip.function( //
        Quantity.of(firstDimMin, firstDimUnit), //
        Quantity.of(firstDimMax, firstDimUnit));
    this.secondDimUnit = secondDimUnit;
    clip1 = Clip.function( //
        Quantity.of(secondDimMin, secondDimUnit), //
        Quantity.of(secondDimMax, secondDimUnit));
    this.outputUnit = outputUnit;
    interpolation = LinearInterpolation.of(Tensors.matrixFloat(table));
  }

  static interface LookupFunction {
    Scalar getValue(Scalar firstValue, Scalar secondValue);
  }

  public LookupTable2D( //
      LookupFunction function, //
      int firstDimN, int secondDimN, //
      Scalar firstDimMin, Scalar firstDimMax, //
      Scalar secondDimMin, Scalar secondDimMax, //
      // Unit firstDimUnit, Unit secondDimUnit, //
      Unit outputUnit) {
    this.originalFunction = function;
    this.firstDimMin = firstDimMin.number().floatValue();
    this.firstDimMax = firstDimMax.number().floatValue();
    this.secondDimMin = secondDimMin.number().floatValue();
    this.secondDimMax = secondDimMax.number().floatValue();
    this.firstDimUnit = Units.of(firstDimMin);
    // firstDimUnit;
    clip0 = Clip.function(firstDimMin, firstDimMax);
    this.secondDimUnit = Units.of(secondDimMin);
    // secondDimUnit;
    clip1 = Clip.function(secondDimMin, secondDimMax);
    table = new float[firstDimN][secondDimN];
    for (int i1 = 0; i1 < firstDimN; ++i1) {
      for (int i2 = 0; i2 < secondDimN; ++i2) {
        float firstValuef = this.firstDimMin + (this.firstDimMax - this.firstDimMin) * i1 / (firstDimN - 1);
        Scalar firstValue = Quantity.of(//
            firstValuef, //
            firstDimUnit);
        float secondValuef = this.secondDimMin + (this.secondDimMax - this.secondDimMin) * i2 / (secondDimN - 1);
        Scalar secondValue = Quantity.of(// ,
            secondValuef, secondDimUnit);
        table[i1][i2] = function.getValue(firstValue, secondValue).number().floatValue();
      }
    }
    this.outputUnit = outputUnit;
    interpolation = LinearInterpolation.of(Tensors.matrixFloat(table));
  }

  /** get inverted lookup table target specifies which of the arguments gets to be
   * the the output: function should be monotone */
  private float getFunctionValue(float x, float y) {
    if (originalFunction != null)
      return originalFunction.getValue( //
          Quantity.of(x, firstDimUnit), //
          Quantity.of(y, secondDimUnit)).number().floatValue();
    throw new UnsupportedOperationException("not tested yet!");
    // return getValue(x, y);
  }

  public LookupTable2D getInverseLookupTableBinarySearch( //
      int target, int firstDimN, int secondDimN, Scalar newDimMin, Scalar newDimMax) {
    float firstDimMinf;
    float firstDimMaxf;
    float secondDimMinf;
    float secondDimMaxf;
    if (target == 0) {
      firstDimMinf = newDimMin.number().floatValue();
      firstDimMaxf = newDimMax.number().floatValue();
      secondDimMinf = secondDimMin;
      secondDimMaxf = secondDimMax;
    } else //
    if (target == 1) {
      firstDimMinf = firstDimMin;
      firstDimMaxf = firstDimMax;
      secondDimMinf = newDimMin.number().floatValue();
      secondDimMaxf = newDimMax.number().floatValue();
    } else
      return null;
    // switch x and out
    float table[][] = new float[firstDimN][secondDimN];
    for (int i1 = 0; i1 < firstDimN; i1++) {
      for (int i2 = 0; i2 < secondDimN; i2++) {
        float firstValuef = firstDimMinf//
            + (firstDimMaxf - firstDimMinf) * i1 / (firstDimN - 1);
        float secondValuef = secondDimMinf//
            + (secondDimMaxf - secondDimMinf) * i2 / (secondDimN - 1);
        // find appropriate value
        // use approximative gradient descent
        float lower;
        float upper;
        float mid = 0;
        if (target == 0) {
          lower = firstDimMin;
          upper = firstDimMax;
          while (Math.abs(upper - lower) > TOLERANCE) {
            mid = (lower + upper) / 2.0f;
            final float midValue = getFunctionValue(mid, secondValuef);
            if (midValue > firstValuef)
              upper = mid;
            else
              lower = mid;
          }
        } else //
        if (target == 1) {
          lower = secondDimMin;
          upper = secondDimMax;
          while (Math.abs(upper - lower) > TOLERANCE) {
            mid = (lower + upper) / 2.0f;
            final float midValue = getFunctionValue(firstValuef, mid);
            if (midValue > secondValuef)
              upper = mid;
            else
              lower = mid;
          }
        }
        table[i1][i2] = mid;
      }
    }
    if (target == 0)
      return new LookupTable2D( //
          table, //
          firstDimMinf, //
          firstDimMaxf, //
          secondDimMinf, //
          secondDimMaxf, //
          outputUnit, //
          secondDimUnit, //
          firstDimUnit);
    if (target == 1)
      return new LookupTable2D(//
          table, //
          firstDimMinf, //
          firstDimMaxf, //
          secondDimMinf, //
          secondDimMaxf, //
          firstDimUnit, //
          outputUnit, //
          secondDimUnit);
    return null;
  }

  private Scalar getLookupValue(Scalar x, Scalar y) {
    int firstDimN = table.length;
    int secondDimN = table[0].length;
    return interpolation.Get(Tensors.of( //
        clip0.rescale(x).multiply(RealScalar.of(firstDimN - 1)), //
        clip1.rescale(y).multiply(RealScalar.of(secondDimN - 1))));
  }

  public Scalar lookup(Scalar x, Scalar y) {
    return Quantity.of(getLookupValue(x, y), outputUnit);
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
