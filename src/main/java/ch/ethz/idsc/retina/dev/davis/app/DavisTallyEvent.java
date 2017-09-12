// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

public class DavisTallyEvent {
  public int[] bin = new int[1600]; // TODO magic const
  public int max = -1;
  public int beg;
  public int end;

  public void register(int index) {
    if (0 <= index && index < bin.length) { // TODO check negative!!!
      ++bin[index];
      max = Math.max(index, max);
    }
  }

  public void setResetBlock(int beg, int end) {
    this.beg = beg;
    this.end = end;
  }
}
