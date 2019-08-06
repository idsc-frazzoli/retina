// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.io.File;
import java.util.Optional;

import ch.ethz.idsc.demo.jg.FileHelper;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;

/* package */ class FileFollowingError extends OfflineFollowingError {
  /** @param curve reference */
  public FileFollowingError(Tensor curve) {
    super();
    setReference(curve);
  }

  public static void main(String[] args) throws Exception {
    System.out.println("choose log file");
    Optional<File> file = FileHelper.open(args);
    System.out.println("choose reference file");
    Optional<File> reference = FileHelper.choose();
    if (file.isPresent() && reference.isPresent()) {
      FileFollowingError followingError = new FileFollowingError(Get.of(reference.get()));
      System.out.print("running... ");
      OfflineLogPlayer.process(file.get(), followingError);
      System.out.println("finished");
      System.out.println(followingError.getReport());
    }
  }
}
