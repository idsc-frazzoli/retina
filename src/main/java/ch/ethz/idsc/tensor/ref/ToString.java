// code by jph
package ch.ethz.idsc.tensor.ref;

import ch.ethz.idsc.tensor.sca.Clip;

public enum ToString {
  ;
  // TODO JPH TENSOR 075 obsolete
  public static String of(Clip clip) {
    return "Clip[" + clip.min() + ", " + clip.max() + "]";
  }
}
