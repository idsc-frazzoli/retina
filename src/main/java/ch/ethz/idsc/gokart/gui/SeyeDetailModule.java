// code by jph
package ch.ethz.idsc.gokart.gui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.demo.jph.davis.Aedat31PolarityImage;
import ch.ethz.idsc.retina.dev.davis.io.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.retina.util.img.ImageCopy;

// TODO
public class SeyeDetailModule extends AbstractModule implements TimedImageListener {
  private final SeyeAeDvsLcmClient seyeAeDvsLcmClient = new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
  private final JFrame jFrame = new JFrame();
  private final ImageCopy imageCopy = new ImageCopy();
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(imageCopy.get(), 0, 0, null);
    }
  };
  private final Aedat31PolarityImage aedat31PolarityImage = //
      new Aedat31PolarityImage(100);

  public SeyeDetailModule() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    aedat31PolarityImage.listeners.add(this);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    seyeAeDvsLcmClient.addDvsListener(aedat31PolarityImage);
    seyeAeDvsLcmClient.startSubscriptions();
    jFrame.setBounds(100, 100, 400, 400);
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    seyeAeDvsLcmClient.stopSubscriptions();
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    // System.out.println("image");
    imageCopy.update(timedImageEvent.bufferedImage);
    jComponent.repaint();
  }

  public static void standalone() throws Exception {
    new SeyeDetailModule().first();
  }

  public static void main(String[] args) throws Exception {
    new SeyeDetailModule().first();
  }
}
