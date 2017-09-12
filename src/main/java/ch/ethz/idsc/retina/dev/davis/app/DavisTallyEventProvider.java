// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.ethz.idsc.retina.dev.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

public class DavisTallyEventProvider {
  public DavisTallyEventListener davisTallyEventListener;
  private DavisTallyEvent davisTallyEvent;
  public int shift = 8;
  public final ColumnTimedImageListener sigListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (isActive()) {
        davisTallyEvent.setMax(time[0]);
        davisTallyEventListener.tallyEvent(davisTallyEvent);
      }
      davisTallyEvent = new DavisTallyEvent(time[0], shift);
      davisTallyEvent.setImageBlock(time[0], time[time.length - 1]);
    }
  };
  public final ColumnTimedImageListener rstListener = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (isActive())
        davisTallyEvent.setResetBlock(time[0], time[time.length - 1]);
    }
  };
  public final DavisDvsEventListener dvsListener = davisDvsEvent -> {
    if (isActive())
      davisTallyEvent.register(davisDvsEvent.time);
  };

  public void setShift(int shift) {
    this.shift = shift;
  }

  public int getShift() {
    return shift;
  }

  boolean isActive() {
    return Objects.nonNull(davisTallyEvent);
  }
}
