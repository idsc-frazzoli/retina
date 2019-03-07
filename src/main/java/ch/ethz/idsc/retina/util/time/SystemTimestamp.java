// code by jph
package ch.ethz.idsc.retina.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum SystemTimestamp {
  ;
  private static final DateFormat FILE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

  public static String asString() {
    return FILE_FORMAT.format(new Date());
  }
}