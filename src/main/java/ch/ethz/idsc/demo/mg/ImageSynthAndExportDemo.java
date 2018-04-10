// code by jph and mg
package ch.ethz.idsc.demo.mg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

// demo code for image generation
enum ImageSynthAndExportDemo {
  ;
  static BufferedImage createImage(int pix) {
    BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 240, 180);
    graphics.setColor(Color.BLACK);
    // graphics.drawRect(pix, 20, 40, 50);
    // for (int i = 0; i < 10; i++) {
    // AffineTransform old = graphics.getTransform();
    // graphics.rotate(Math.toRadians(45), 110, 90);
    // graphics.draw(new Ellipse2D.Float(100, 50, 20, 80));
    // graphics.setTransform(old);
    // }
    // code to check if ellipse is drawn correctly
    double val1 = 2500;
    double val2 = 1000;
    Tensor matrix = Tensors.matrixDouble(new double[][] { { 0.4 * val1, -300 }, { -300, 0.4 * val2 } });
    // find eigenvector belonging to first eigenvalue
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor firstEigVec = eigensystem.vectors().get(0);
    // find rotation angle of that eigenvector
    // TODO mario, Math.atan2(y, x) vs. ArcTan.of(x, y)
    double rotAngle = Math.atan2(firstEigVec.Get(1).number().doubleValue(), firstEigVec.Get(0).number().doubleValue());
    System.out.println("FirstEigVec: " + firstEigVec.Get(0).number().doubleValue() + "/" + firstEigVec.Get(1).number().doubleValue());
    System.out.println("Rot Angle [deg]" + rotAngle * 180 / Math.PI);
    Tensor semiAxes = Sqrt.of(eigensystem.values());
    System.out.println("FristEigVal: " + semiAxes.Get(0).number().floatValue());
    float leftCornerX = 150 - semiAxes.Get(0).number().floatValue();
    float leftCornerY = 150 - semiAxes.Get(1).number().floatValue();
    // draw ellipse with first eigenvalue aligned with x axis
    Ellipse2D ellipse = new Ellipse2D.Float(leftCornerX, leftCornerY, //
        2 * semiAxes.Get(0).number().floatValue(), //
        2 * semiAxes.Get(1).number().floatValue());
    // rotate around blob mean by rotAngle which is the angle between first eigenvector and x axis
    graphics.rotate(rotAngle, 150, 150);
    graphics.drawRect(148, 148, 4, 4);
    graphics.draw(ellipse);
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    // for (int index = 0; index < 6000; ++index)
    // bytes[index] = (byte) index;
    return bufferedImage;
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = createImage(10);
    int count = 0;
    ImageIO.write(bufferedImage, "png", UserHome.Pictures(String.format("example%03d.png", count)));
  }
}
