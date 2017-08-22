// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

public abstract class DavisApsCorrection {
  /** alignment column 0, column 1, ... */
  final int[] pitchblack;
  int count = -1;

  public DavisApsCorrection(int[] pitchblack) {
    this.pitchblack = pitchblack;
  }

  public int nextReference() {
    return pitchblack[++count];
  }

  public void reset() {
    count = -1;
  }
}
