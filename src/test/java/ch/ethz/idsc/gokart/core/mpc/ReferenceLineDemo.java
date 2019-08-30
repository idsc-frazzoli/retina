// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum ReferenceLineDemo {
  ;
  public static void main(String[] args) {
    try {
      Tensor ref = ReferenceTrajectory.of("fielddata700.csv", 300, 1);
      System.out.println(ref);
      Export.of(HomeDirectory.Documents("ReferenceTrajectory", "traj.csv"), ref.map(Magnitude.METER));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
