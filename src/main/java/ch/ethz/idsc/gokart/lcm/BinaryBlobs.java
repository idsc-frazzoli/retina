// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.util.Arrays;

import idsc.BinaryBlob;

public enum BinaryBlobs {
  ;
  public static BinaryBlob create(int length) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = new byte[length];
    return binaryBlob;
  }

  public static BinaryBlob create(byte[] data) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = data.length;
    binaryBlob.data = data;
    return binaryBlob;
  }

  public static BinaryBlob create(byte[] data, int length) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = Arrays.copyOf(data, length);
    return binaryBlob;
  }
}
