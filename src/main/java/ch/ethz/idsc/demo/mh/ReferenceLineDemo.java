// code by mh
package ch.ethz.idsc.demo.mh;

import ch.ethz.idsc.gokart.core.mpc.ReferenceTrajectory;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum ReferenceLineDemo {
  ;
  public static void main(String[] args) {
    try {
      Tensor ref = ReferenceTrajectory.of("track_20190318T143816.csv", 1000, 1);
      System.out.println(ref);
      Export.of(HomeDirectory.Documents("ReferenceTrajectory", "traj.csv"), ref.map(Magnitude.METER));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
