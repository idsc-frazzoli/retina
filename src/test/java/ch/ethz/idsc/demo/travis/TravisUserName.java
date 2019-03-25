// code by jph
package ch.ethz.idsc.demo.travis;

import ch.ethz.idsc.tensor.io.UserName;

/** class only for testing purpose */
public enum TravisUserName {
  ;
  /** @return true if username is travis */
  public static boolean whoami() {
    return UserName.is("travis");
  }
}
