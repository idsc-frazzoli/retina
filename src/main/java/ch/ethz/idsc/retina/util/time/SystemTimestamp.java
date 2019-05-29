// code by jph
package ch.ethz.idsc.retina.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum SystemTimestamp {
  ;
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

  /** @param date
   * @return "20190529T141738" for example */
  public static String asString(Date date) {
    return DATE_FORMAT.format(date);
  }

  public static String asString() {
    return asString(new Date());
  }
}