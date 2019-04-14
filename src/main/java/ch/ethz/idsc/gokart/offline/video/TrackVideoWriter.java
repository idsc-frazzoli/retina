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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

/** implementation renders a log file to a mp4 video */
public class TrackVideoWriter implements OfflineLogListener, AutoCloseable {
  private final Tensor model2pixel;
  private final BufferedImage background;
  private final String poseChannel;
  // ---
  private final Mp4AnimationWriter mp4AnimationWriter;
  private final TrackVideoRender trackVideoRender;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;

  /** @param backgroundImage
   * @param trackVideoConfig
   * @param file with extension "mp4"
   * @throws Exception */
  public TrackVideoWriter(BackgroundImage backgroundImage, TrackVideoConfig trackVideoConfig, File file) //
      throws Exception {
    this.model2pixel = backgroundImage.model2pixel;
    this.background = backgroundImage.bufferedImage;
    this.poseChannel = trackVideoConfig.poseChannel;
    Dimension dimension = new Dimension(background.getWidth(), background.getHeight());
    mp4AnimationWriter = new Mp4AnimationWriter( //
        file.toString(), //
        dimension, //
        Magnitude.PER_SECOND.toInt(trackVideoConfig.frameRate));
    trackVideoRender = new TrackVideoRender(model2pixel, poseChannel);
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
      graphics.drawImage(background, 0, 0, null);
      trackVideoRender.render(GeometricLayer.of(model2pixel), graphics);
      graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
      graphics.setColor(Color.GRAY);
      graphics.drawString(String.format("time :%9s", time.map(Round._2)), 0, 25);
      mp4AnimationWriter.append(bufferedImage);
    }
  }

  @Override // from AutoCloseable
  public void close() {
    mp4AnimationWriter.close();
  }
}
