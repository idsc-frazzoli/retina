// code by jph
package ch.ethz.idsc.retina.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.se2.rrts.TransitionNdContainer;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.sophus.app.api.ClothoidDisplay;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.AnimationWriter;

public enum TransitionNdVideo {
  ;
  public static void main(String[] args) throws InterruptedException, Exception {
    Tensor lbounds = Tensors.vector(0, 0).unmodifiable();
    Tensor ubounds = Tensors.vector(12.8, 7.2).unmodifiable();
    TransitionNdContainer transitionNdDemo = new TransitionNdContainer(lbounds, ubounds, 500, 50);
    GeometricLayer geometricLayer = GeometricLayer.of(Tensors.fromString("{{150, 0, 0}, {0, -150, 1080}, {0, 0, 1}}"));
    BufferedImage bufferedImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    final int length = 10;
    try (AnimationWriter animationWriter = new Mp4AnimationWriter("/home/datahaki/video.mp4", new Dimension(1920, 1080), 15)) {
      double angle = 0;
      for (Tensor _x : Subdivide.of(0, 1 * length, 100 * length)) {
        double t = _x.Get().number().doubleValue();
        System.out.println(t);
        angle += SimplexContinuousNoise.FUNCTION.at(t, t) * 0.1;
        Tensor mouse = Tensors.vectorDouble( //
            (SimplexContinuousNoise.FUNCTION.at(t) + 1) / 2 * 7 + ((12.8 - 7) / 2), //
            (SimplexContinuousNoise.FUNCTION.at(0, t) + 1) / 2 * 7, //
            angle);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        transitionNdDemo.render( //
            ClothoidDisplay.INSTANCE, //
            geometricLayer, //
            graphics, //
            mouse);
        animationWriter.write(bufferedImage);
      }
    }
    // ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("spider.png"));
  }
}
