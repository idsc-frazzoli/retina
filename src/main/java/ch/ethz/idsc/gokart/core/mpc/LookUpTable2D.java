package ch.ethz.idsc.gokart.core.mpc;

import java.io.BufferedReader;
import java.io.IOException;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

public abstract class LookUpTable2D {
  final float table[][];
  final int firstDimN;
  final int secondDimN;
  final float firstDimMin;
  final float firstDimMax;
  final float secondDimMin;
  final float secondDimMax;
  final Unit firstDimUnit;
  final Unit secondDimUnit;
  final Unit outputUnit;

  public LookUpTable2D(BufferedReader csvReader) throws IOException {
    String line;
    // read dimensions
    firstDimN = Integer.parseInt(csvReader.readLine());
    secondDimN = Integer.parseInt(csvReader.readLine());
    table = new float[firstDimN][secondDimN];
    line = csvReader.readLine();
    // read limits
    String[] firstLimits = line.split(",");
    firstDimMin = Integer.parseInt(firstLimits[0]);
    firstDimMax = Integer.parseInt(firstLimits[1]);
    line = csvReader.readLine();
    String[] secondLimits = line.split(",");
    secondDimMin = Integer.parseInt(secondLimits[0]);
    secondDimMax = Integer.parseInt(secondLimits[1]);
    // read units
    firstDimUnit = Unit.of(csvReader.readLine());
    secondDimUnit = Unit.of(csvReader.readLine());
    outputUnit = Unit.of(csvReader.readLine());
    for (int i1 = 0; i1 < firstDimN; i1++) {
      line = csvReader.readLine();
      String[] linevals = line.split(",");
      for (int i2 = 0; i2 < secondDimN; i2++) {
        table[i1][i2] = Float.parseFloat(linevals[i2]);
      }
    }
  }

  private float getValue(float x, float y) {
    float posx = (x - firstDimMin) / (firstDimMax - firstDimMin) * (firstDimN - 1);
    float posy = (y - secondDimMin) / (secondDimMin - secondDimMin) * (secondDimN - 1);
    if (posx < firstDimMin)
      posx = firstDimMin;
    if (posx > firstDimMax)
      posx = firstDimMax;
    if (posy < secondDimMin)
      posy = secondDimMin;
    if (posy > secondDimMin)
      posy = secondDimMin;
    int firstFrom = (int) Math.floor(posx);
    int firstTo = (int) Math.ceil(posx);
    int secondFrom = (int) Math.floor(posx);
    int secondTo = (int) Math.ceil(posx);
    float firstProg = (posx - firstFrom);
    float secondProg = (posy - secondFrom);
    // interpolate
    return //
    (1 - firstProg) * (1 - secondProg) * table[firstFrom][secondFrom]// 1
        + firstProg * (1 - secondProg) * table[firstTo][secondFrom]// 2
        + (1 - firstProg) * secondProg * table[firstFrom][secondTo]// 3
        + firstProg * secondProg * table[firstTo][secondTo];// 4
  }

  public Scalar lookup(Scalar x, Scalar y) {
    float fx = x.number().floatValue();
    float fy = y.number().floatValue();
    return Quantity.of(//
        getValue(fx,fy), outputUnit);
  }
}
