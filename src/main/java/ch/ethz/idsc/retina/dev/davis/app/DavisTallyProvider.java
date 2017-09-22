// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

public class DavisTallyProvider {
  private final DavisTallyListener davisTallyListener;
  private DavisTallyEvent davisTallyEvent;
  public int shift = 8;
  public final ColumnTimedImageListener sigListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (isTriggered()) {
        davisTallyEvent.setMax(time[0]);
        davisTallyListener.tallyEvent(davisTallyEvent);
      }
      davisTallyEvent = new DavisTallyEvent(time[0], shift);
      davisTallyEvent.setImageBlock(time[0], time[time.length - 1]);
    }
  };
  public final ColumnTimedImageListener rstListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (isTriggered())
        davisTallyEvent.setResetBlock(time[0], time[time.length - 1]);
    }
  };
  public final DavisDvsListener dvsListener = davisDvsEvent -> {
    if (isTriggered())
      davisTallyEvent.register(davisDvsEvent.time);
  };

  public DavisTallyProvider(DavisTallyListener davisTallyListener) {
    this.davisTallyListener = davisTallyListener;
  }

  public void setShift(int shift) {
    this.shift = shift;
  }

  public int getShift() {
    return shift;
  }

  /* package */ boolean isTriggered() { // 2017
    return Objects.nonNull(davisTallyEvent);
  }
}
