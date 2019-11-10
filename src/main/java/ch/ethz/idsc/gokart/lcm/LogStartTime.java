// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.io.File;
import java.io.IOException;

import lcm.logging.Log;

public enum LogStartTime {
  ;
  /** @param file
   * @return
   * @throws IOException */
  public static long utime(File file) throws IOException {
    try (Log log = new Log(file.toString(), "r")) {
      long utime = log.readNext().utime;
      return utime;
    }
  }
}
