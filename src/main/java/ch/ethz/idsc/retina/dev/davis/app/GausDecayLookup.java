// code by az and jph
package ch.ethz.idsc.retina.dev.davis.app;

public class GausDecayLookup {
  private final byte[] array;

  /** @param max for lookup in the interval [0, ..., max - 1] */
  public GausDecayLookup(int length, double factor, double polarity) {
    array = new byte[length + 1];
    for (int delta = 0; delta <= length; ++delta) {
      double normts = 1.0 - delta / (double) length;
      double scaledts = -factor * normts;
      double decayedts = Math.exp(-Math.pow(scaledts, 2));
      double grayscale = 127.5 * (1 + decayedts * polarity);
      array[delta] = (byte) grayscale;
    }
  }

  public byte get(int delta) {
    return array[delta];
  }
}
