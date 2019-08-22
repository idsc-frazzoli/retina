// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum MultiLogFollowingError {
  ;

  private static final String[] ERROR_TYPES = {"position error", "heading error"};
  private static final Scalar[] BIN_SIZES = { Quantity.of(0.01, "m"), RealScalar.of(0.01)};

  public static void main(String[] args) throws Exception {
    Arrays.stream(args).forEach(System.out::println);
    Iterator<String> iterator = Arrays.asList(args).iterator();
    List<String> sources = new ArrayList<>();
    List<Tensor> errors  = new ArrayList<>();
    while (iterator.hasNext()) {
      LogFollowingError followingError = new LogFollowingError();
      OfflineLogPlayer.process(new File(iterator.next()), followingError);
      errors.add(Transpose.of(followingError.errors()));
      followingError.getReport().ifPresent(System.out::println);
    }
    ErrorDistributions.plot(errors.stream().toArray(Tensor[]::new), sources.toArray(new String[sources.size()]), ERROR_TYPES, BIN_SIZES);
    sources.add(iterator.next());
  }
}
