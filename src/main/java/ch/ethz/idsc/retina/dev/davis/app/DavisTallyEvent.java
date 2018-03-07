// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.util.IntRange;

public class DavisTallyEvent {
  public final int first;
  private int last;
  public final int shift; // 2^shift
  public final int[] binPlus = new int[1600]; // TODO magic const
  public final int[] binMinus = new int[1600]; // TODO magic const
  public int binPlusLast = -1;
  public int binMinusLast = -1;
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

  public void register(int time) {
    int index = binIndex(time);
    if (0 <= index && index < binPlus.length) {
      ++binPlus[index];
      binPlusLast = Math.max(binPlusLast, index);
    }
    if (0 <= index && index < binMinus.length) {
        ++binMinus[index];
        binMinusLast = Math.max(binMinusLast, index);
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
    if (0 <= index && index < binPlus.length) {
      binPlusLast = index;
    }
    if (0 <= index && index < binMinus.length) {
        binMinusLast = index;
      }
  }

  public int getDurationUs() {
    return last - first;
  }
}
