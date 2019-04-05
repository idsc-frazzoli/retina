// code by jph
package ch.ethz.idsc.demo.jph.video;

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
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

public class TrackVideoRender implements OfflineLogListener, AutoCloseable {
  private final Tensor model2pixel;
  private final BufferedImage background;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final Mp4AnimationWriter mp4AnimationWriter;
  private final String poseChannel;
  private final OfflineRender offlineRender;

  public TrackVideoRender(Tensor model2pixel, BufferedImage background, String poseChannel, File file) throws Exception {
    this.model2pixel = model2pixel;
    this.background = background;
    this.poseChannel = poseChannel;
    Dimension dimension = new Dimension(background.getWidth(), background.getHeight());
    mp4AnimationWriter = new Mp4AnimationWriter(file.toString(), dimension, StaticHelper.FRAMERATE);
    bufferedImage = new BufferedImage( //
        dimension.width, //
        dimension.height, //
        BufferedImage.TYPE_3BYTE_BGR);
    offlineRender = new OfflineRender(model2pixel, poseChannel);
    graphics = bufferedImage.createGraphics();
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    // if (Scalars.lessEquals(Quantity.of(3, SI.SECOND), time))
    // return;
    // ---
    offlineRender.event(time, channel, byteBuffer);
    if (channel.equals(poseChannel)) {
      graphics.drawImage(background, 0, 0, null);
      offlineRender.render(GeometricLayer.of(model2pixel), graphics);
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
