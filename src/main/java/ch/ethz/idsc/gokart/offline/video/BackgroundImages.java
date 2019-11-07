// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.gokart.gui.top.GeneralImageRender;
import ch.ethz.idsc.gokart.gui.top.Rieter;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

public enum BackgroundImages {
  ;
  public static BackgroundImage rieter(Tensor model2pixel) {
    BufferedImage bufferedImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    {
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    }
    {
      BackgroundImage backgroundImage = Rieter.backgroundImage20191104();
      GeneralImageRender generalImageRender = new GeneralImageRender(backgroundImage.bufferedImage(), Inverse.of(backgroundImage.model2pixel()));
      generalImageRender.render(GeometricLayer.of(model2pixel), graphics);
    }
    FadeTop.of(bufferedImage);
    return new BackgroundImage(bufferedImage, model2pixel);
  }
}
