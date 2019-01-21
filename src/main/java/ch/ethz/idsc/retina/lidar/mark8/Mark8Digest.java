// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

/** interface for data processing of mark8 sensor packets */
@FunctionalInterface
public interface Mark8Digest {
  byte[] digest(byte[] data);
}
