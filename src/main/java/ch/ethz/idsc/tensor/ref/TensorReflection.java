// code by jph
package ch.ethz.idsc.tensor.ref;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Clip;

public enum TensorReflection {
  ;
  /** @param fieldSubdivide
   * @return Optional.empty() if given fieldSubdivide is null,
   * or fields specified by given fieldSubdivide are invalid */
  public static Optional<Tensor> of(FieldSubdivide fieldSubdivide) {
    if (Objects.nonNull(fieldSubdivide))
      try {
        return Optional.of(Subdivide.of( //
            Tensors.fromString(fieldSubdivide.start()), //
            Tensors.fromString(fieldSubdivide.end()), //
            fieldSubdivide.intervals()));
      } catch (Exception exception) {
        // ---
      }
    return Optional.empty();
  }

  /** @param fieldClip
   * @return Optional.empty() if given fieldClip is null,
   * or fields specified by given fieldClip are invalid */
  public static Optional<Clip> of(FieldClip fieldClip) {
    if (Objects.nonNull(fieldClip))
      try {
        return Optional.of(Clip.function( //
            Scalars.fromString(fieldClip.min()), //
            Scalars.fromString(fieldClip.max())));
      } catch (Exception exception) {
        // ---
      }
    return Optional.empty();
  }
}
