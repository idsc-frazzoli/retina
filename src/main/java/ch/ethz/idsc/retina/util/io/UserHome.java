// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;

public enum UserHome {
  ;
  /** for the special input filename == ""
   * the function returns the user home directory
   * 
   * @param filename
   * @return */
  public static File file(String filename) {
    return new File(System.getProperty("user.home"), filename);
  }

  public static File Pictures(String filename) {
    return new File(file("Pictures"), filename);
  }
}
