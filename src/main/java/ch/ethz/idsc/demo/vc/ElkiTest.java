// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.gokart.core.perc.Clusters;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum ElkiTest {
  ;
  public static void main(String[] args) {
    double[][] data = new double[10][2];
    data[3][0] = 0.98;
    data[3][1] = 0.1;
    data[0][0] = 0.99;
    data[0][1] = 0.16;
    data[2][0] = 2.16;
    data[2][1] = 3.99;
    data[1][0] = 2.1;
    data[1][1] = 3.98;
    Tensor p = Tensors.matrixDouble(data);
    Tensor testDBSCANResults = Clusters.elkiDBSCAN(p);
  }
}
