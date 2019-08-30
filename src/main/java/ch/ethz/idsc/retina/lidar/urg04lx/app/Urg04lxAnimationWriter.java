// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx.app;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxRangeEvent;
import ch.ethz.idsc.retina.lidar.urg04lx.Urg04lxRangeListener;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

public class Urg04lxAnimationWriter implements Urg04lxRangeListener {
  private final AnimationWriter animationWriter;
  private final Dimension dimension;
  private final BufferedImage image;
  private int frames = 0;
  private final Urg04lxRender urg04lxRender = new Urg04lxRender();

  public Urg04lxAnimationWriter(File file, int period, Dimension dimension) throws Exception {
    animationWriter = new GifAnimationWriter(file, period, TimeUnit.MILLISECONDS);
    this.dimension = dimension;
    image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    urg04lxRender.setZoom(-3);
  }

  @Override
  public void urg04lxRange(Urg04lxRangeEvent urg04lxEvent) {
    urg04lxRender.setEvent(urg04lxEvent);
    urg04lxRender.render(image.createGraphics(), dimension);
    try {
      animationWriter.write(image);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    ++frames;
  }

  public int frameCount() {
    return frames;
  }

  public void close() throws Exception {
    animationWriter.close();
    System.out.println("closed gif");
  }
}
