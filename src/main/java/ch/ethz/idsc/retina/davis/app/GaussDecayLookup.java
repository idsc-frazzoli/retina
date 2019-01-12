// code by az
package ch.ethz.idsc.retina.davis.app;

/* package */ class GaussDecayLookup {
  private final byte[] array;

  /** @param length
   * @param factor
   * @param polarity */
  public GaussDecayLookup(int length, double factor, double polarity) {
    array = new byte[length + 1];
    for (int delta = 0; delta <= length; ++delta) {
      double normts = 1.0 - delta / (double) length;
      double scaledts = -factor * normts;
      double decayedts = Math.exp(-scaledts * scaledts); // Math.pow(scaledts, 2)
      double grayscale = 127.5 * (1 + decayedts * polarity);
      array[delta] = (byte) grayscale;
    }
  }

  public byte get(int delta) {
    return array[delta];
  }
}
