// code by jph
package ch.ethz.idsc.demo.mg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.demo.mg.util.VisualizationUtil;

/** demo to test ellipse merging */
class ImageDisplayFrame {
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  private JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
    }
  };

  public ImageDisplayFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jPanelMain = new JPanel(new BorderLayout());
    {
      JPanel jPanelTop = new JPanel(new BorderLayout());
    }
    jPanelMain.add("Center", jComponent);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 400, 400);
    jFrame.setVisible(true);
  }

  public void setImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    jComponent.repaint();
  }

  // testing
  private static float[] mergePos(float[] posA, float actA, float[] posB, float actB) {
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

  static double[][] steinerCov(double[][] covariance, float[] displacement, float activity) {
    double[][] steinerCov = new double[2][2];
    steinerCov[0][0] = covariance[0][0] + activity * displacement[1] * displacement[1];
    steinerCov[1][1] = covariance[1][1] + activity * displacement[0] * displacement[0];
    steinerCov[0][1] = covariance[0][1] - activity * displacement[0] * displacement[1];
    steinerCov[1][0] = steinerCov[0][1];
    return steinerCov;
  }

  static double[][] addCov(double[][] firstCov, double[][] secondCov) {
    double[][] addCov = new double[2][2];
    addCov[0][0] = firstCov[0][0] + secondCov[0][0];
    addCov[0][1] = firstCov[0][1] + secondCov[0][1];
    addCov[1][1] = firstCov[1][1] + secondCov[1][1];
    addCov[1][0] = addCov[0][1];
    return addCov;
  }

  public static void main(String[] args) throws InterruptedException {
    ImageDisplayFrame imageDisplayFrame = new ImageDisplayFrame();
    BufferedImage bufferedImage = new BufferedImage(400, 400, BufferedImage.TYPE_BYTE_INDEXED);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 400, 400);
    float firstActivity = 1;
    float secondActivity = 2;
    // first blob
    ImageBlob firstBlob = new ImageBlob(new float[] { 150, 150 }, null, 0, false, 0);
    firstBlob.setCovariance(1500, 1800, 0.3);
    VisualizationUtil.drawImageBlob(graphics, firstBlob, Color.BLUE);
    // second blob
    ImageBlob secondBlob = new ImageBlob(new float[] { 250, 250 }, null, 0, false, 0);
    secondBlob.setCovariance(500, 1300, 0.6);
    VisualizationUtil.drawImageBlob(graphics, secondBlob, Color.BLUE);
    // merge for third blob
    float[] mergedPos = mergePos(firstBlob.getPos(), firstActivity, secondBlob.getPos(), secondActivity);
    double[][] mergedCov = mergeCovB(firstBlob.getCovariance(), firstActivity, secondBlob.getCovariance(), secondActivity);
    // new steiner merge
    float[] firstDisplacement = new float[] { mergedPos[0] - firstBlob.getPos()[0], mergedPos[1] - firstBlob.getPos()[1] };
    float[] secondDisplacement = new float[] { mergedPos[0] - secondBlob.getPos()[0], mergedPos[1] - secondBlob.getPos()[1] };
    double[][] firstmergedCovSteiner = steinerCov(firstBlob.getCovariance(), firstDisplacement, firstActivity);
    double[][] secondMergedCovSteiner = steinerCov(secondBlob.getCovariance(), secondDisplacement, secondActivity);
    double[][] mergedCovSteiner = addCov(firstmergedCovSteiner, secondMergedCovSteiner);
    ImageBlob mergedBlob = new ImageBlob(mergedPos, mergedCovSteiner, 0, false, 0);
    VisualizationUtil.drawImageBlob(graphics, mergedBlob, Color.RED);
    imageDisplayFrame.setImage(bufferedImage);
  }
}
