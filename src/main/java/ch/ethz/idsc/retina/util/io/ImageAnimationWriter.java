// code by jph
package ch.ethz.idsc.retina.util.io;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import ch.ethz.idsc.tensor.io.AnimationWriter;

/** for use with animated GIF's or MP4 */
public class ImageAnimationWriter implements Consumer<BufferedImage> {
  private final AnimationWriter animationWriter;

  public ImageAnimationWriter(AnimationWriter animationWriter) {
    this.animationWriter = animationWriter;
  }

  @Override // from Consumer
  public void accept(BufferedImage bufferedImage) {
    try {
      animationWriter.append(bufferedImage);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
