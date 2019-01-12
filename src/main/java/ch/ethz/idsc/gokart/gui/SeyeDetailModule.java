// code by jph
package ch.ethz.idsc.gokart.gui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.demo.jph.davis.Aedat31PolarityImage;
import ch.ethz.idsc.gokart.lcm.seye.SeyeAeDvsLcmClient;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class SeyeDetailModule extends AbstractModule implements TimedImageListener {
  public static final int PACKETS = 200;
  private final SeyeAeDvsLcmClient seyeAeDvsLcmClient = new SeyeAeDvsLcmClient(GokartLcmChannel.SEYE_OVERVIEW);
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final ImageCopy imageCopy = new ImageCopy();
  private final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(imageCopy.get(), 0, 0, null);
    }
  };
  private final Aedat31PolarityImage aedat31PolarityImage = new Aedat31PolarityImage(Color.DARK_GRAY, PACKETS);

  @Override // from AbstractModule
  protected void first() throws Exception {
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    aedat31PolarityImage.listeners.add(this);
    seyeAeDvsLcmClient.addDvsListener(aedat31PolarityImage);
    seyeAeDvsLcmClient.startSubscriptions();
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    seyeAeDvsLcmClient.stopSubscriptions();
  }

  @Override // from TimedImageListener
  public void timedImage(TimedImageEvent timedImageEvent) {
    imageCopy.update(timedImageEvent.bufferedImage);
    jComponent.repaint();
  }

  public static void standalone() throws Exception {
    SeyeDetailModule autoboxCompactModule = new SeyeDetailModule();
    autoboxCompactModule.first();
    autoboxCompactModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
