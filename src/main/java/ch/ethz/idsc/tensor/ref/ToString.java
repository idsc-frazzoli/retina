// code by jph
package ch.ethz.idsc.tensor.ref;

import ch.ethz.idsc.tensor.sca.Clip;

public enum ToString {
  ;
  public static String of(Clip clip) {
    return "Clip[" + clip.min() + ", " + clip.max() + "]";
  }
}
