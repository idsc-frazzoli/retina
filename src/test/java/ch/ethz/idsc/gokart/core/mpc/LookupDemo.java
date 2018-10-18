package ch.ethz.idsc.gokart.core.mpc;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
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
      Tensor speeds = Subdivide.of(-5, 5, 500).map(s -> Quantity.of(s, SI.VELOCITY));
      Tensor matrix = Tensors.matrix((i, j) -> PowerHelpers.getAccelerationEstimation(powers.Get(i), speeds.Get(j)), powers.length(), speeds.length());
      Tensor rgba = ArrayPlot.of(matrix, ColorDataGradients.THERMOMETER);
      Export.of(UserHome.Pictures("linearinterplook2d.png"), rgba);
    }
    // Export.of(UserHome.Pictures("linearinterplook2d.csv"), rgba);
    // Tensor tensor = Import.of(UserHome.Pictures("linearinterplook2d.csv"));
    // @SuppressWarnings("unused")
    // float[][] array = Primitives.toFloatArray2D(tensor);4
  }
}
