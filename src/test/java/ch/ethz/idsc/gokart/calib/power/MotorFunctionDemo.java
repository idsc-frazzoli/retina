// code by mh
package ch.ethz.idsc.gokart.calib.power;

import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ enum MotorFunctionDemo {
  ;
  private static final int RES = 500 - 1;

  @SuppressWarnings("unchecked")
  static <T extends Tensor> Tensor build(BiFunction<T, T, ? extends Tensor> function, Tensor vi, Tensor vj) {
    return Parallelize.matrix((i, j) -> function.apply((T) vi.get(i), (T) vj.get(j)), vi.length(), vj.length());
  }

  static void visualize(MotorFunctionBase motorFunctionBase) throws IOException {
    String string = motorFunctionBase.getClass().getSimpleName();
    File dir = HomeDirectory.Pictures(string);
    dir.mkdir();
    BinaryOperator<Scalar> function = motorFunctionBase::getAccelerationEstimation;
    Clip clip_powers = ManualConfig.GLOBAL.torqueLimitClip();
    final Tensor powers = Subdivide.increasing(clip_powers, RES);
    Clip clip_speeds = Clips.absolute(Quantity.of(+10, SI.VELOCITY));
    final Tensor speeds = Subdivide.increasing(clip_speeds, RES);
    // TODO JPH potentially increase to 2.1
    Clip clip_accels = Clips.absolute(Quantity.of(+2, SI.ACCELERATION));
    final Tensor accelerations = Subdivide.increasing(clip_accels, RES);
    {
      Tensor matrix = MotorFunctionDemo.build(function, powers.negate(), speeds);
      Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
      Export.of(new File(dir, "linearinterplook2d.png"), rgba);
    }
    {
      final int dimN = 250;
      LookupTable2D lookUpTable2D = LookupTable2D.build(//
          function, //
          dimN, dimN, //
          clip_powers, //
          clip_speeds);
      LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch( //
          function, 0, //
          dimN, dimN, //
          clip_accels, Chop._03);
      System.out.println("max acc at v=1 :" + lookUpTable2D.getExtremalValues0(Quantity.of(1, SI.VELOCITY)));
      System.out.println("max arms at v=1 :" + inverseLookupTable.getExtremalValues0(Quantity.of(1, SI.VELOCITY)));
      System.out.println("set up inverse table");
      {
        Tensor matrix = MotorFunctionDemo.build(lookUpTable2D::lookup, powers.negate(), speeds);
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(new File(dir, "lookupTable.png"), rgba);
      }
      {
        Tensor matrix = MotorFunctionDemo.build(inverseLookupTable::lookup, accelerations.negate(), speeds);
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(new File(dir, "inverseTable.png"), rgba);
      }
    }
    System.out.println("exported pictures");
  }

  public static void main(String[] args) throws IOException {
    visualize(MotorFunctionV1.INSTANCE);
    visualize(MotorFunctionV2.INSTANCE);
  }
}
