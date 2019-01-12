// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.Polygons;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class UpdatedMap {
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final TensorUnaryOperator toPixel;
  private Tensor polygon = Tensors.empty();
  private int count = 0;

  public UpdatedMap() {
    ImageCopy imageCopy = new ImageCopy();
    PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
    imageCopy.update(predefinedMap.getImage());
    toPixel = toPixel(predefinedMap.getModel2Pixel());
    bufferedImage = imageCopy.get();
    graphics = bufferedImage.createGraphics();
  }

  public void setCrop(Tensor polygon) {
    this.polygon = Tensor.of(polygon.stream().map(toPixel));
  }

  public void intake(Tensor model2pixel, List<Tensor> list) {
    GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
    graphics.setColor(Color.WHITE);
    for (Tensor pnts : list)
      for (Tensor x : pnts) {
        Point2D point2D = geometricLayer.toPoint2D(x);
        Tensor vector = Tensors.vector(point2D.getX(), point2D.getY());
        if (Polygons.isInside(polygon, vector))
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 1, 1);
      }
  }

  public void store() {
    File file = HomeDirectory.Pictures(String.format("map%04d.png", count));
    System.out.println(file);
    try {
      ImageIO.write(bufferedImage, "png", file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    ++count;
  }

  private static TensorUnaryOperator toPixel(Tensor model2pixel) {
    return new TensorUnaryOperator() {
      @Override
      public Tensor apply(Tensor model) {
        return model2pixel.dot(model.copy().append(RealScalar.ONE)).extract(0, 2);
      }
    };
  }

  public boolean nonEmpty() {
    return Tensors.nonEmpty(polygon);
  }
}
