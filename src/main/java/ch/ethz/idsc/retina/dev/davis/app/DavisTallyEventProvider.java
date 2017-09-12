// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

public class DavisTallyEventProvider {
  private int shift = 8; // 2^shift
  private Integer left = null;
  public DavisTallyEventListener intArrayListener;
  private DavisTallyEvent davisTallyEvent = new DavisTallyEvent();
  public final ColumnTimedImageListener sigListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      left = time[0];
      intArrayListener.tallyEvent(davisTallyEvent);
      reset();
    }
  };
  public final ColumnTimedImageListener rstListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (isActive())
        davisTallyEvent.setResetBlock(binIndex(time[0]), binIndex(time[time.length - 1]));
    }
  };
  public final DavisDvsEventListener dvsListener = davisDvsEvent -> registerEvent(davisDvsEvent.time);

  public void registerEvent(int time) {
    if (isActive()) {
      int index = binIndex(time);
      davisTallyEvent.register(index);
    }
  }

  public void reset() {
    davisTallyEvent = new DavisTallyEvent();
  }

  public void setShift(int shift) {
    this.shift = shift;
  }

  int binIndex(int time) {
    time -= left;
    return time >> shift;
  }

  public int getShift() {
    return shift;
  }

  public boolean isActive() {
    return Objects.nonNull(left);
  }
}
