// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

/* package */ enum PoseQualityFailures {
  ;
  private static final Scalar THRESHOLD = RealScalar.of(0.5);

  public static void main(String[] args) throws IOException {
    String pose = GokartPoseChannel.INSTANCE.exportName() + StaticHelper.EXTENSION;
    String post = GokartPosePostChannel.INSTANCE.exportName() + StaticHelper.EXTENSION;
    for (File date : StaticHelper.DEST.listFiles())
      for (File directory : date.listFiles()) {
        // System.out.println(directory);
        Tensor tensor1 = Import.of(new File(directory, pose));
        Tensor tensor2 = Import.of(new File(directory, post));
        {
          long count1 = tensor1.stream().filter(row -> Scalars.lessThan(row.Get(4), THRESHOLD)).count();
          long count2 = tensor2.stream().filter(row -> Scalars.lessThan(row.Get(4), THRESHOLD)).count();
          if (0 < count1) {
            System.out.println(directory.getName());
            System.out.println(count1 + " " + count2);
          }
        }
      }
  }
}
