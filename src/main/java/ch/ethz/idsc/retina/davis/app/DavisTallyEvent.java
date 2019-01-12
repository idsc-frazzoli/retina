// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.retina.util.IntRange;

public class DavisTallyEvent {
  private static final int SIZE_MAX = 1600;
  // ---
  public final int first;
  private int last;
  public final int shift; // 2^shift
  public final int[][] bin = new int[SIZE_MAX][2];
  public int binLast = -1;
  public IntRange resetRange = new IntRange(0, 0);
  public IntRange imageRange = null;

  public DavisTallyEvent(int first, int shift) {
    this.first = first;
    this.shift = shift;
  }

  private int binIndex(int time) {
    time -= first;
    return time >> shift;
  }

  /** @param time
   * @param i either 0 or 1
   * @throws Exception if input is out of valid range */
  public void register(int time, int i) {
    int index = binIndex(time);
    if (0 <= index && index < bin.length) {
      ++bin[index][i];
      binLast = Math.max(binLast, index);
    }
  }

  public void setResetBlock(int beg, int end) {
    try {
      resetRange = new IntRange(binIndex(beg), binIndex(end));
    } catch (Exception exception) {
      System.err.println("timing !");
    }
  }

  public void setImageBlock(int beg, int end) {
    try {
      imageRange = new IntRange(binIndex(beg), binIndex(end));
    } catch (Exception exception) {
      System.err.println("timing !");
    }
  }

  public void setMax(int time) {
    last = time;
    int index = binIndex(time);
    if (0 <= index && index < bin.length) {
      binLast = index;
    }
  }

  public int getDurationUs() {
    return last - first;
  }
}
