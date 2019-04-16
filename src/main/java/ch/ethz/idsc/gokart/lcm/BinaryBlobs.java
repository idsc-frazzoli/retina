// code by jph
package ch.ethz.idsc.gokart.lcm;

import idsc.BinaryBlob;

public enum BinaryBlobs {
  ;
  public static BinaryBlob create(int length) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = new byte[length];
    return binaryBlob;
  }
}
