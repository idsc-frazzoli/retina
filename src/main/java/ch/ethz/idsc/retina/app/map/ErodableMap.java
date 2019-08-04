// code by jph
package ch.ethz.idsc.retina.app.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;

public class ErodableMap implements RenderInterface {
  /** fine */
  private final BufferedImage bufferedImage;
  private final int width;
  private final int height;
  private final byte[] data;
  private final Graphics2D graphics;
  private final Tensor pixel2model;
  private final GeometricLayer geometricLayer;

  /** @param bufferedImage
   * @param pixel2model */
  public ErodableMap(BufferedImage bufferedImage, Tensor pixel2model) {
    this.bufferedImage = bufferedImage;
    width = bufferedImage.getWidth();
    height = bufferedImage.getHeight();
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    graphics = bufferedImage.createGraphics();
    this.pixel2model = pixel2model;
    geometricLayer = GeometricLayer.of(Inverse.of(pixel2model));
    geometricLayer.pushMatrix(IdentityMatrix.of(3));
  }

  public void setReference(Tensor matrix) {
    geometricLayer.popMatrix();
    geometricLayer.pushMatrix(matrix);
  }

  public void setPixel(Tensor tensor, boolean occupy) {
    setPixel( //
        tensor.Get(0).number().doubleValue(), //
        tensor.Get(1).number().doubleValue(), //
        occupy);
  }

  public void setPixel(double px, double py, boolean occupy) {
    Point2D point2d = geometricLayer.toPoint2D(px, py);
    graphics.setColor(occupy ? Color.WHITE : Color.BLACK);
    // fillRect is more precise than fill(new Rectangle...)
    graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 1, 1);
  }

  /** @param radius 0 means no erosion
   * @return */
  /* package */ BufferedImage createErodedMap(double radius) {
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    double rad = radius + 0.5;
    double wid = rad * 2;
    Graphics2D graphics2d = bufferedImage.createGraphics();
    graphics2d.setColor(Color.WHITE);
    int index = 0;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        if (data[index] != 0)
          graphics2d.fill(new Ellipse2D.Double(x - rad, y - rad, wid, wid));
        ++index;
      }
    }
    return bufferedImage;
  }

  public BufferedImageRegion erodedRegion(double radius) {
    return new BufferedImageRegion(createErodedMap(radius), pixel2model, true);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(pixel2model);
    graphics.drawImage(bufferedImage, //
        AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
    geometricLayer.popMatrix();
  }

  public BufferedImage bufferedImage() {
    return bufferedImage;
  }
}
