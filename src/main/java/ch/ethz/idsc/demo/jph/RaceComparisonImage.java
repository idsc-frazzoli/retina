// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class RaceComparisonImage {
  private final BackgroundImage backgroundImage;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final GeometricLayer geometricLayer;

  public RaceComparisonImage() throws IOException {
    backgroundImage = VideoBackground.get20190701();
    bufferedImage = backgroundImage.bufferedImage();
    graphics = bufferedImage.createGraphics();
    geometricLayer = GeometricLayer.of(backgroundImage.model2pixel());
  }

  private void draw(RaceRenderImage raceRenderImage, int pix, String string) {
    raceRenderImage.render(geometricLayer, graphics);
    BufferedImage icon = ResourceData.bufferedImage("/image/driver/" + string + ".png");
    graphics.drawImage(icon, pix, 0, null);
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 52));
    graphics.drawString("" + raceRenderImage.duration(), pix + 80, 0 + 55);
    graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 36));
    graphics.drawString("Vx_max=" + raceRenderImage.maxVx(), pix + 80, 55 + 55);
    graphics.drawString("Vy_max=" + raceRenderImage.maxVy(), pix + 80, 55 + 55 + 40);
    graphics.drawString("Vw_max=" + raceRenderImage.maxVw(), pix + 80, 55 + 55 + 40 + 40);
  }

  public static void main(String[] args) throws IOException {
    RaceComparisonImage raceComparisonImage = new RaceComparisonImage();
    {
      Scalar duration = Quantity.of(11.6336, SI.SECOND);
      File folder = new File("/media/datahaki/data/gokart/0701hum/20190701T170957_04");
      RaceRenderImage raceRenderImage = new RaceRenderImage( //
          Quantity.of(58.5, SI.SECOND), //
          duration, new Color(0, 0, 255, 128));
      OfflineLogPlayer.process(new File(folder, "log.lcm"), raceRenderImage);
      raceComparisonImage.draw(raceRenderImage, 0, "ch");
    }
    {
      Scalar duration = Quantity.of(12.2325, SI.SECOND);
      File folder = new File("/media/datahaki/data/gokart/0701mpc/20190701T175650_01");
      RaceRenderImage raceRenderImage = new RaceRenderImage( //
          Quantity.of(16.8986, SI.SECOND), //
          duration, new Color(255, 0, 0, 128));
      OfflineLogPlayer.process(new File(folder, "log.lcm"), raceRenderImage);
      raceRenderImage.render(raceComparisonImage.geometricLayer, raceComparisonImage.graphics);
      raceComparisonImage.draw(raceRenderImage, 960, "tg");
    }
    // Tensor tensor = offlineTableSupplier.getTable();
    // System.out.println(Dimensions.of(tensor));
    ImageIO.write(raceComparisonImage.bufferedImage, "png", HomeDirectory.file("combined.png"));
  }
}
