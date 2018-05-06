// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;

public enum VoidScatterImage implements ScatterImage {
  INSTANCE;
  @Override
  public void render(Tensor model, Tensor points) {
    // ---
  }

  @Override
  public BufferedImage getImage() {
    return null;
  }
}
