// code by mh
package ch.ethz.idsc.gokart.calib.power;

import java.io.IOException;
import java.util.function.BiFunction;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ enum LookupDemo {
  ;
  private static final int RES = 500 - 1;

  @SuppressWarnings("unchecked")
  private static <T extends Tensor> Tensor build(BiFunction<T, T, ? extends Tensor> function, Tensor vi, Tensor vj) {
    return Tensors.matrix((i, j) -> function.apply((T) vi.get(i), (T) vj.get(j)), vi.length(), vj.length());
  }

  public static void main(String[] args) throws IOException {
    Clip clip_powers = ManualConfig.GLOBAL.torqueLimitClip();
    final Tensor powers = Subdivide.of(clip_powers, RES);
    Clip clip_speeds = Clip.function( //
        Quantity.of(-10, SI.VELOCITY), //
        Quantity.of(+10, SI.VELOCITY));
    final Tensor speeds = Subdivide.of(clip_speeds, RES);
    Clip clip_accels = Clip.function( //
        Quantity.of(-2, SI.ACCELERATION), //
        Quantity.of(+2, SI.ACCELERATION));
    final Tensor accelerations = Subdivide.of(clip_accels, RES);
    {
      Tensor matrix = build(MotorFunction::getAccelerationEstimation, powers.negate(), speeds);
      Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
      Export.of(HomeDirectory.Pictures("linearinterplook2d.png"), rgba);
    }
    {
      final int dimN = 250;
      LookupTable2D lookUpTable2D = LookupTable2D.build(//
          MotorFunction::getAccelerationEstimation, //
          dimN, //
          dimN, //
          clip_powers, //
          clip_speeds);
      LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch( //
          MotorFunction::getAccelerationEstimation, 0, //
          dimN, dimN, //
          clip_accels, Chop._03);
      System.out.println("max acc at v=1 :" + lookUpTable2D.getExtremalValues(0, Quantity.of(1, SI.VELOCITY)));
      System.out.println("max arms at v=1 :" + inverseLookupTable.getExtremalValues(0, Quantity.of(1, SI.VELOCITY)));
      System.out.println("set up inverse table");
      {
        Tensor matrix = build(lookUpTable2D::lookup, powers.negate(), speeds);
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(HomeDirectory.Pictures("lookupTable.png"), rgba);
      }
      {
        Tensor matrix = build(inverseLookupTable::lookup, accelerations.negate(), speeds);
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(HomeDirectory.Pictures("inverseTable.png"), rgba);
      }
      // System.out.println(inverseLookupTable.lookup(Quantity.of(number, string), y));
    }
    System.out.println("exported pictures");
  }
}
