// code by jph
package ch.ethz.idsc.retina.davis.app;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.davis.io.DavisExportControl;
import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;

/** export control that is enabled from the 2nd image onwards */
public class FirstImageTriggerExportControl implements DavisExportControl, ColumnTimedImageListener {
  private int frames = 0;
  private int time_offset = 0;

  @Override
  public void columnTimedImage(ColumnTimedImage columnTimedImage) {
    if (1 == frames) {
      GlobalAssert.that(!isActive());
      time_offset = columnTimedImage.time[0];
      System.out.println("enabled at " + time_offset);
    }
    ++frames;
  }

  @Override
  public boolean isActive() {
    return 2 < frames;
  }

  @Override
  public int mapTime(int time) {
    int mapped = time - time_offset;
    if (mapped <= 0)
      System.err.println("mapped=" + mapped);
    // GlobalAssert.that(0 <= mapped);
    return mapped;
  }
}
