// code by jph
package ch.ethz.idsc.retina.sys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemTimestamp {
  private static final DateFormat FILE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

  public static String file() {
    return FILE_FORMAT.format(new Date());
  }
}