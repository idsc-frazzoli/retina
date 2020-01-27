// code by gjoel
package ch.ethz.idsc.gokart.dev.led;

import com.dalsemi.onewire.utils.CRC8;

/* package */ enum Crc8MaximHelper {
  ;

  public static byte[] convert(int[] array) {
    byte[] bytesi = new byte[array.length];
    byte[] byteso = new byte[array.length + 1];
    for (int i = 0; i < array.length; i++) {
      bytesi[i] = (byte) array[i];
      byteso[i] = (byte) array[i];
    }
    byteso[array.length] = (byte) CRC8.compute(bytesi);
    return byteso;
  }
}
