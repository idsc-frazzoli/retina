// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Round;

/** implementation renders a log file to a mp4 video */
public class TrackVideoWriter implements OfflineLogListener, AutoCloseable {
  private final BackgroundImage backgroundImage;
  private final TrackVideoConfig trackVideoConfig;
  private final String poseChannel;
  // ---
  private final Mp4AnimationWriter mp4AnimationWriter;
  private final TrackVideoRender trackVideoRender;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private int frame = 0;

  /** @param backgroundImage
   * @param trackVideoConfig
   * @param file with extension "mp4"
   * @throws Exception */
  public TrackVideoWriter(BackgroundImage backgroundImage, TrackVideoConfig trackVideoConfig, File file) //
      throws Exception {
    this.backgroundImage = backgroundImage;
    this.trackVideoConfig = trackVideoConfig;
    this.poseChannel = trackVideoConfig.poseChannel;
    Dimension dimension = backgroundImage.dimension();
    mp4AnimationWriter = new Mp4AnimationWriter( //
        file.toString(), //
        dimension, //
        Magnitude.PER_SECOND.toInt(trackVideoConfig.frameRate));
    trackVideoRender = new TrackVideoRender(backgroundImage.model2pixel(), poseChannel);
    if (0 < trackVideoConfig.lidarPoints)
      trackVideoRender.lidarPointsRender(backgroundImage.model2pixel(), trackVideoConfig.lidarPoints);
    bufferedImage = new BufferedImage( //
        dimension.width, //
        dimension.height, //
        BufferedImage.TYPE_3BYTE_BGR);
    graphics = bufferedImage.createGraphics();
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    trackVideoRender.event(time, channel, byteBuffer);
    if (channel.equals(poseChannel)) {
      graphics.drawImage(backgroundImage.bufferedImage(), 0, 0, null);
      trackVideoRender.render(GeometricLayer.of(backgroundImage.model2pixel()), graphics);
      graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
      graphics.setColor(Color.GRAY);
      graphics.drawString(String.format("time :%9s", time.map(Round._2)), 0, 25);
      mp4AnimationWriter.write(bufferedImage);
      System.out.println(time.map(Round._3));
      if (trackVideoConfig.frameLimit < ++frame)
        throw new RuntimeException();
    }
  }

  @Override // from AutoCloseable
  public void close() {
    mp4AnimationWriter.close();
  }
}
