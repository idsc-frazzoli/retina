// code by jph
package ch.ethz.idsc.retina.util.io;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import ch.ethz.idsc.tensor.io.AnimationWriter;

/** for use MP4
 * internally converts images to 3-byte BGR since this is accepted by
 * {@link Mp4AnimationWriter} */
public class BGR3ByteAnimationWriter implements Consumer<BufferedImage> {
  private final AnimationWriter animationWriter;
  // ---
  private int count = 0;

  public BGR3ByteAnimationWriter(AnimationWriter animationWriter) {
    this.animationWriter = animationWriter;
  }

  @Override // from Consumer
  public void accept(BufferedImage bufferedImage) {
    try {
      if (++count % 1000 == 0)
        System.out.println("frame " + count);
      BufferedImage frame = new BufferedImage( //
          bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
      frame.createGraphics() //
          .drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
      animationWriter.append(frame);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
