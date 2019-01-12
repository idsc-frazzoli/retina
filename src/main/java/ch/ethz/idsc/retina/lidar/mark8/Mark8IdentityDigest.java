// code by jph
package ch.ethz.idsc.retina.lidar.mark8;

/** publishes mark8 as-is resulting in about 6+ MB/sec */
public enum Mark8IdentityDigest implements Mark8Digest {
  INSTANCE;
  // ---
  @Override
  public byte[] digest(byte[] data) {
    return data;
  }
}
