// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.IOException;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

enum LookupDemo {
  ;
  private static final int RES = 500 - 1;

  private static Tensor build(BinaryOperator<Scalar> function, Tensor vi, Tensor vj) {
    return Tensors.matrix((i, j) -> function.apply(vi.Get(i), vj.Get(j)), vi.length(), vj.length());
  }

  public static void main(String[] args) throws IOException {
    Clip clip_powers = ManualConfig.GLOBAL.torqueLimitClip();
    final Tensor powers = Subdivide.of( //
        clip_powers.min(), //
        clip_powers.max(), RES);
    Clip clip_speeds = Clip.function( //
        Quantity.of(-10, SI.VELOCITY), //
        Quantity.of(+10, SI.VELOCITY));
    final Tensor speeds = Subdivide.of( //
        clip_speeds.min(), //
        clip_speeds.max(), RES);
    final Tensor accelerations = Subdivide.of( //
        Quantity.of(-2, SI.ACCELERATION), //
        Quantity.of(+2, SI.ACCELERATION), RES);
    {
      Tensor matrix = build(MotorFunction::getAccelerationEstimation, powers.negate(), speeds);
      Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
      Export.of(UserHome.Pictures("linearinterplook2d.png"), rgba);
    }
    // Export.of(UserHome.Pictures("linearinterplook2d.csv"), rgba);
    // Tensor tensor = Import.of(UserHome.Pictures("linearinterplook2d.csv"));
    // @SuppressWarnings("unused")
    // float[][] array = Primitives.toFloatArray2D(tensor);4
    {
      final int DimN = 250;
      // final Scalar xMin = ManualConfig.GLOBAL.torqueLimit.negate();
      // final Scalar xMax = ManualConfig.GLOBAL.torqueLimit;
      final Scalar yMin = Quantity.of(-10, SI.VELOCITY);
      final Scalar yMax = Quantity.of(+10, SI.VELOCITY);
      LookupTable2D lookUpTable2D = LookupTable2D.build(//
          MotorFunction::getAccelerationEstimation, //
          DimN, //
          DimN, //
          clip_powers, //
          clip_speeds, //
          SI.ACCELERATION);
      LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch( //
          MotorFunction::getAccelerationEstimation, //
          0, //
          DimN, //
          DimN, //
          Quantity.of(-2, SI.ACCELERATION), //
          Quantity.of(+2, SI.ACCELERATION));
      System.out.println("max acc at v=1 :" + lookUpTable2D.getExtremalValues(0, Quantity.of(1, SI.VELOCITY)));
      System.out.println("max arms at v=1 :" + inverseLookupTable.getExtremalValues(0, Quantity.of(1, SI.VELOCITY)));
      System.out.println("set up inverse table");
      {
        Tensor matrix = build(lookUpTable2D::lookup, powers.negate(), speeds);
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(UserHome.Pictures("lookupTable.png"), rgba);
      }
      {
        Tensor matrix = build(inverseLookupTable::lookup, accelerations.negate(), speeds);
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(UserHome.Pictures("inverseTable.png"), rgba);
      }
      // System.out.println(inverseLookupTable.lookup(Quantity.of(number, string), y));
    }
    System.out.println("exported pictures");
  }
}
