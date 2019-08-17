// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.davis._240c.Davis240c;
import ch.ethz.idsc.retina.davis.app.TriggeredAccumulatedImage;
import ch.ethz.idsc.retina.davis.data.DavisDvsDatagramDecoder;

public class AccumulatedImageRender implements RenderInterface {
  public static final int HEIGHT = 270;
  final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private final TriggeredAccumulatedImage triggeredAccumulatedImage = new TriggeredAccumulatedImage(Davis240c.INSTANCE);

  public AccumulatedImageRender() {
    davisDvsDatagramDecoder.addDvsListener(triggeredAccumulatedImage);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(triggeredAccumulatedImage.bufferedImage(), 1920 - 360, 0, 360, HEIGHT, null);
    triggeredAccumulatedImage.clearImage();
  }
}
