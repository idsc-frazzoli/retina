//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.List;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.dev.davis.data.DavisDvsDatagramDecoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;

// submodule filters event stream and allows visualization
public class InputSubModule implements OfflineLogListener, DavisDvsListener {
  private final DavisDvsDatagramDecoder davisDvsDatagramDecoder = new DavisDvsDatagramDecoder();
  private DavisSurfaceOfActiveEvents surface = new DavisSurfaceOfActiveEvents();
  private DavisBlobTracker track;
  private final int maxEventCount = 10000;
  private final int backgroundActivityFilterTime = 1000; // [us] the shorter the more is filtered
  private final int imageInterval = 100; // [us]
  private boolean useFilter;
  private int eventCount;
  private int filteredEventCount;
  private int lastTimestamp;
  private int imageCount;

  public InputSubModule() {
    davisDvsDatagramDecoder.addDvsListener(this);
    track = new DavisBlobTracker();
    useFilter = true;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals("davis240c.overview.dvs")) {
      davisDvsDatagramDecoder.decode(byteBuffer);
    }
  }

  @Override
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    ++eventCount;
    track.receiveNewEvent(davisDvsEvent);
    if ((davisDvsEvent.time - lastTimestamp) > imageInterval) {
      try {
        generateImage();
      } catch (IOException e) {
        e.printStackTrace();
      }
      lastTimestamp = davisDvsEvent.time;
    }
    if (surface.backgroundActivityFilter(davisDvsEvent, backgroundActivityFilterTime) && useFilter) {
      ++filteredEventCount;
    }
    if (eventCount > maxEventCount) {
      System.exit(0);
    }
  }

  private void generateImage() throws IOException {
    List<DavisSingleBlob> activeBlobs = track.getActiveBlobs();
    if (activeBlobs.size() == 0) {
      System.out.println("********No active blob present");
      return;
    }
    //TODO collect all events in the update interval and visualize them as well
    
    //TODO generate a window and show the images in a stream
    System.out.println("****generating image");
    BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 240, 180);
    graphics.setColor(Color.BLACK);
    for (int i = 0; i < activeBlobs.size(); i++) {
      Ellipse2D e = new Ellipse2D.Float(activeBlobs.get(i).getPos()[0], activeBlobs.get(i).getPos()[1], activeBlobs.get(i).getSemiAxes()[1],
          activeBlobs.get(i).getSemiAxes()[0]);
      AffineTransform old = graphics.getTransform();
      graphics.rotate(activeBlobs.get(i).getRotAngle(), activeBlobs.get(i).getPos()[0] + 0.5 * activeBlobs.get(i).getSemiAxes()[1],
          activeBlobs.get(i).getPos()[1] + 0.5 * activeBlobs.get(i).getSemiAxes()[0]);
      graphics.draw(e);
      graphics.setTransform(old);
    }
    ++imageCount;
    System.out.printf("Image saved as example%03d.png\n", imageCount);
    ImageIO.write(bufferedImage, "png", UserHome.Pictures(String.format("example%03d.png", imageCount)));
  }

  public double getFilteredPercentage() {
    return 100 * (1 - filteredEventCount / eventCount);
  }
}
