// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.IOException;

import ch.ethz.idsc.gokart.core.mpc.LookupTable2D.LookupFunction;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.qty.Quantity;

enum LookupDemo {
  ;
  public static void main(String[] args) throws IOException {
    {
      Tensor powers = Subdivide.of(-2300, 2300, 500).map(s -> Quantity.of(s, NonSI.ARMS));
      Tensor speeds = Subdivide.of(-10, 10, 500).map(s -> Quantity.of(s, SI.VELOCITY));
      Tensor matrix = Tensors.matrix((i, j) -> MotorFunction.getAccelerationEstimation(powers.Get(i).negate(), speeds.Get(j)), powers.length(),
          speeds.length());
      Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
      Export.of(UserHome.Pictures("linearinterplook2d.png"), rgba);
    }
    // Export.of(UserHome.Pictures("linearinterplook2d.csv"), rgba);
    // Tensor tensor = Import.of(UserHome.Pictures("linearinterplook2d.csv"));
    // @SuppressWarnings("unused")
    // float[][] array = Primitives.toFloatArray2D(tensor);4
    {
      LookupFunction function = new LookupFunction() {
        @Override
        public Scalar getValue(Scalar firstValue, Scalar secondValue) {
          // power, Speed
          return MotorFunction.getAccelerationEstimation(firstValue, secondValue);
        }
      };
      final int DimN = 250;
      final Scalar xMin = Quantity.of(-2300, NonSI.ARMS);
      final Scalar xMax = Quantity.of(2300, NonSI.ARMS);
      final Scalar yMin = Quantity.of(-10, SI.VELOCITY);
      final Scalar yMax = Quantity.of(10, SI.VELOCITY);
      LookupTable2D lookUpTable2D = new LookupTable2D(//
          function, //
          DimN, //
          DimN, //
          xMin, //
          xMax, //
          yMin, //
          yMax, //
          SI.ACCELERATION);
      LookupTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch(//
          0, //
          DimN, //
          DimN, //
          Quantity.of(-2, SI.ACCELERATION), //
          Quantity.of(2, SI.ACCELERATION));
      System.out.println("max acc at v=1 :" + lookUpTable2D.getExtremalValues(0, Quantity.of(1, SI.VELOCITY)));
      System.out.println("max arms at v=1 :" + inverseLookupTable.getExtremalValues(0, Quantity.of(1, SI.VELOCITY)));
      System.out.println("set up inverse table");
      {
        Tensor accelerations = Subdivide.of(-2300, 2300, 500).map(s -> Quantity.of(s, NonSI.ARMS));
        Tensor speeds = Subdivide.of(-8, 8, 500).map(s -> Quantity.of(s, SI.VELOCITY));
        Tensor matrix = Tensors.matrix((i, j) -> lookUpTable2D.lookup(accelerations.Get(i).negate(), speeds.Get(j)), accelerations.length(), speeds.length());
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(UserHome.Pictures("lookupTable.png"), rgba);
      }
      {
        Tensor accelerations = Subdivide.of(-2, 2, 500).map(s -> Quantity.of(s, SI.ACCELERATION));
        Tensor speeds = Subdivide.of(-5, 5, 500).map(s -> Quantity.of(s, SI.VELOCITY));
        Tensor matrix = Tensors.matrix((i, j) -> inverseLookupTable.lookup(accelerations.Get(i).negate(), speeds.Get(j)), accelerations.length(),
            speeds.length());
        Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
        Export.of(UserHome.Pictures("inverseTable.png"), rgba);
      }
      System.out.println("exported pictures");
      // System.out.println(inverseLookupTable.lookup(Quantity.of(number, string), y));
    }
  }
}
