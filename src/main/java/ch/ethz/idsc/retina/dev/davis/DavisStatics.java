// code by jph
package ch.ethz.idsc.retina.dev.davis;

import java.nio.ByteOrder;

public enum DavisStatics {
  ;
  /** on most modern PC's the default order is LITTLE_ENDIAN */
  public static final ByteOrder BYTE_ORDER = ByteOrder.LITTLE_ENDIAN;

  public static void main(String[] args) {
    System.out.println("ByteOrder.nativeOrder() == " + ByteOrder.nativeOrder());
  }
}
