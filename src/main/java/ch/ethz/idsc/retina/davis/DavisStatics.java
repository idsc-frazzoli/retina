// code by jph
package ch.ethz.idsc.retina.davis;

import java.nio.ByteOrder;

public enum DavisStatics {
  ;
  /** on most modern PC's the default order is LITTLE_ENDIAN */
  public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;
  /** for an image of width == 240 column is in {2, 3, 4, 5, 6, 8, 10, ... }
   * FactorInteger[240] == {{2, 4}, {3, 1}, {5, 1}}
   * 
   * @param columns
   * has to divide image width */
  public static final int APS_COLUMNS = 8;
}
