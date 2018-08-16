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

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.demo.mg.util.vis.VisPipelineUtil;

/** demo to test ellipse merging */
class ImageDisplayFrame {
  private final JFrame jFrame = new JFrame();
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, null);
    }
  };

  public ImageDisplayFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jPanelMain = new JPanel(new BorderLayout());
    jPanelMain.add("Center", jComponent);
    jFrame.setContentPane(jPanelMain);
    jFrame.setBounds(100, 100, 400, 400);
    jFrame.setVisible(true);
  }

  public void setImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    jComponent.repaint();
  }

  public static void main(String[] args) {
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
    VisPipelineUtil.drawImageBlob(graphics, firstBlob, Color.BLUE);
    // second blob
    ImageBlob secondBlob = new ImageBlob(new float[] { 250, 250 }, null, 0, false, 0);
    secondBlob.setCovariance(500, 1300, 0.6);
    VisPipelineUtil.drawImageBlob(graphics, secondBlob, Color.BLUE);
    // merge for third blob
    float[] mergedPos = StaticHelper.mergePos(firstBlob.getPos(), firstActivity, secondBlob.getPos(), secondActivity);
    double[][] mergedCov = StaticHelper.mergeCovB(firstBlob.getCovariance(), firstActivity, secondBlob.getCovariance(), secondActivity);
    // new steiner merge
    float[] firstDisplacement = new float[] { mergedPos[0] - firstBlob.getPos()[0], mergedPos[1] - firstBlob.getPos()[1] };
    float[] secondDisplacement = new float[] { mergedPos[0] - secondBlob.getPos()[0], mergedPos[1] - secondBlob.getPos()[1] };
    double[][] firstmergedCovSteiner = StaticHelper.steinerCov(firstBlob.getCovariance(), firstDisplacement, firstActivity);
    double[][] secondMergedCovSteiner = StaticHelper.steinerCov(secondBlob.getCovariance(), secondDisplacement, secondActivity);
    double[][] mergedCovSteiner = StaticHelper.addCov(firstmergedCovSteiner, secondMergedCovSteiner);
    ImageBlob mergedBlob = new ImageBlob(mergedPos, mergedCovSteiner, 0, false, 0);
    VisPipelineUtil.drawImageBlob(graphics, mergedBlob, Color.RED);
    imageDisplayFrame.setImage(bufferedImage);
  }
}
