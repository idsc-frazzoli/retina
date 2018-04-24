// code by jph and mg
package ch.ethz.idsc.demo.mg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

// dirty demo code to illustrate ellipse merging
public enum ImageSynthAndExportDemo {
  ;
  public static BufferedImage createImage(int pix) {
    BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_INDEXED);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 240, 180);
    graphics.setColor(Color.BLACK);
    // first ellipse
    float[] posA = { 50, 50 };
    double[][] covA = { { 1000, 300 }, { 300, 1500 } };
    float activityA = 100;
    // second ellipse
    float[] posB = { 150, 100 };
    double[][] covB = { { 800, -200 }, { -200, 1000 } };
    float activityB = 200;
    AffineTransform old = graphics.getTransform();
    createEllipse(posA, covA, graphics);
    graphics.setTransform(old);
    createEllipse(posB, covB, graphics);
    graphics.setTransform(old);
    float[] posC = mergePos(posA, activityA, posB, activityB);
    double[][] covC = mergeCovA(covA, activityA, covB, activityB);
    graphics.setColor(Color.RED);
    createEllipse(posC, covC, graphics);
    // WritableRaster writableRaster = bufferedImage.getRaster();
    // DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    // byte[] bytes = dataBufferByte.getData();
    // for (int index = 0; index < 6000; ++index)
    // bytes[index] = (byte) index;
    return bufferedImage;
  }

  static void createEllipse(float[] pos, double[][] covariance, Graphics2D graphics) {
    Tensor matrix = Tensors.matrixDouble(covariance);
    // find eigenvector belonging to first eigenvalue
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor firstEigVec = eigensystem.vectors().get(0);
    // find rotation angle of that eigenvector
    double rotAngle = Math.atan2(firstEigVec.Get(1).number().doubleValue(), firstEigVec.Get(0).number().doubleValue());
    // System.out.println("FirstEigVec: " + firstEigVec.Get(0).number().doubleValue() + "/" + firstEigVec.Get(1).number().doubleValue());
    // System.out.println("Rot Angle [deg]" + rotAngle * 180 / Math.PI);
    Tensor semiAxes = Sqrt.of(eigensystem.values());
    // System.out.println("FristEigVal: " + semiAxes.Get(0).number().floatValue());
    float leftCornerX = pos[0] - semiAxes.Get(0).number().floatValue();
    float leftCornerY = pos[1] - semiAxes.Get(1).number().floatValue();
    // draw ellipse with first eigenvalue aligned with x axis
    Ellipse2D ellipse = new Ellipse2D.Float(leftCornerX, leftCornerY, //
        2 * semiAxes.Get(0).number().floatValue(), //
        2 * semiAxes.Get(1).number().floatValue());
    // rotate around blob mean by rotAngle which is the angle between first eigenvector and x axis
    graphics.rotate(rotAngle, pos[0], pos[1]);
    graphics.draw(ellipse);
  }

  static float[] mergePos(float[] posA, float actA, float[] posB, float actB) {
    float[] pos = new float[2];
    pos[0] = 1 / (actA + actB) * (actA * posA[0] + actB * posB[0]);
    pos[1] = 1 / (actA + actB) * (actA * posA[1] + actB * posB[1]);
    return pos;
  }

  static double[][] mergeCovA(double[][] covA, float actA, double[][] covB, float actB) {
    double[][] cov = new double[2][2];
    cov[0][0] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[0][0] + actB * actB * covB[0][0]);
    cov[0][1] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[0][1] + actB * actB * covB[0][1]);
    cov[1][0] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[1][0] + actB * actB * covB[1][0]);
    cov[1][1] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[1][1] + actB * actB * covB[1][1]);
    return cov;
  }

  static double[][] mergeCovB(double[][] covA, float actA, double[][] covB, float actB) {
    double[][] cov = new double[2][2];
    cov[0][0] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[0][0] + actB * actB * covB[0][0]);
    cov[0][1] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[0][1] + actB * actB * covB[0][1]);
    cov[1][0] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[1][0] + actB * actB * covB[1][0]);
    cov[1][1] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[1][1] + actB * actB * covB[1][1]);
    return cov;
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = createImage(10);
    int count = 0;
    // ImageIO.write(bufferedImage, "png", UserHome.Pictures(String.format("example%03d.png", count)));
  }
}
