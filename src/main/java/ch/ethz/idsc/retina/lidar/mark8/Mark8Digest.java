// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

/** interface for data processing of mark8 sensor packets */
public interface Mark8Digest {
  byte[] digest(byte[] data);
}
