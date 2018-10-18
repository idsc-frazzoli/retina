package ch.ethz.idsc.gokart.core.mpc;

import java.io.IOException;

import ch.ethz.idsc.gokart.core.mpc.LookUpTable2D.LookupFunction;
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
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.qty.Quantity;

enum LookupDemo {
  ;
  public static void main(String[] args) throws IOException {
    {
      Tensor powers = Subdivide.of(-2300, 2300, 500).map(s -> Quantity.of(s, NonSI.ARMS));
      Tensor speeds = Subdivide.of(-8, 8, 500).map(s -> Quantity.of(s, SI.VELOCITY));
      Tensor matrix = Tensors.matrix((i, j) -> PowerHelpers.getAccelerationEstimation(powers.Get(i).negate(), speeds.Get(j)), powers.length(), speeds.length());
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
          return PowerHelpers.getAccelerationEstimation(firstValue, secondValue);
        }
      };
      final int DimN = 100;
      final Scalar xMin = Quantity.of(-10, SI.VELOCITY);
      final Scalar xMax = Quantity.of(10, SI.VELOCITY);
      final Scalar yMin = Quantity.of(-2300, NonSI.ARMS);
      final Scalar yMax = Quantity.of(2300, NonSI.ARMS);
      LookUpTable2D lookUpTable2D = new LookUpTable2D(//
          function, //
          DimN, //
          DimN, //
          xMin, //
          xMax, //
          yMin, //
          yMax, //
          NonSI.ARMS, SI.VELOCITY, SI.ACCELERATION);
      LookUpTable2D inverseLookupTable = lookUpTable2D.getInverseLookupTableBinarySearch(//
          0, //
          DimN, //
          DimN, //
          Quantity.of(-2, SI.ACCELERATION), //
          Quantity.of(2, SI.ACCELERATION), //
          Quantity.of(-5, SI.VELOCITY), //
          Quantity.of(5, SI.VELOCITY));
      System.out.println("set up inverse table");
      Tensor accelerations = Subdivide.of(-2, 2, 500).map(s -> Quantity.of(s, SI.ACCELERATION));
      Tensor speeds = Subdivide.of(-5, 5, 500).map(s -> Quantity.of(s, SI.VELOCITY));
      Tensor matrix = Tensors.matrix((i, j) -> inverseLookupTable.lookup(accelerations.Get(i).negate(), speeds.Get(j)), accelerations.length(),
          speeds.length());
      Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
      Export.of(UserHome.Pictures("inverseTable.png"), rgba);
      // System.out.println(inverseLookupTable.lookup(Quantity.of(number, string), y));
    }
  }
}
