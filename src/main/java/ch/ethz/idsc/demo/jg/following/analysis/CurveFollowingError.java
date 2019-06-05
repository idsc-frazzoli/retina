// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.io.File;
import java.util.Optional;

import ch.ethz.idsc.demo.jg.FileHelper;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;

/* package */ class CurveFollowingError extends OfflineFollowingError {
  /** @param curve reference */
  public CurveFollowingError(Tensor curve) {
    super();
    setReference(curve);
  }

  public static void main(String[] args) throws Exception {
    Optional<File> file = FileHelper.open(args);
    Optional<File> reference = FileHelper.choose();
    if (file.isPresent() && reference.isPresent()) {
      CurveFollowingError followingError = new CurveFollowingError(Get.of(reference.get()));
      System.out.print("running... ");
      OfflineLogPlayer.process(file.get(), followingError);
      System.out.println("finished");
      System.out.println(followingError.getReport());
    }
  }
}
