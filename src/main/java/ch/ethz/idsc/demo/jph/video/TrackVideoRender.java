// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class TrackVideoRender implements OfflineLogListener, RenderInterface, AutoCloseable {
  private final BufferedImage background;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final Mp4AnimationWriter mp4AnimationWriter;

  public TrackVideoRender(BufferedImage background, File file) throws Exception {
    this.background = background;
    Dimension dimension = new Dimension(background.getWidth(), background.getHeight());
    mp4AnimationWriter = new Mp4AnimationWriter(file.toString(), dimension, StaticHelper.FRAMERATE);
    bufferedImage = new BufferedImage( //
        dimension.width, //
        dimension.height, //
        BufferedImage.TYPE_3BYTE_BGR);
    graphics = bufferedImage.createGraphics();
  }

  private Scalar time;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (Scalars.lessEquals(Quantity.of(5, SI.SECOND), time))
      return;
    // ---
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GeometricLayer geometricLayer = GeometricLayer.of(VideoBackground.MODEL2PIXEL); // TODO
      this.time = time;
      render(geometricLayer, graphics);
      mp4AnimationWriter.append(bufferedImage);
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.drawImage(background, 0, 0, null);
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.drawString(String.format("time:%8s", time.map(Round._3)), 0, 25);
  }

  @Override // from AutoCloseable
  public void close() {
    mp4AnimationWriter.close();
  }

  public static void main(String[] args) throws Exception {
    BufferedImage background = ImageIO.read(VideoBackground.IMAGE_FILE);
    try (TrackVideoRender trackVideoRender = new TrackVideoRender( //
        background, //
        HomeDirectory.file("test.mp4"))) {
      OfflineLogPlayer.process(TrackDrivingTables.SINGLETON, trackVideoRender);
    }
    System.out.println("[done.]");
  }
}
