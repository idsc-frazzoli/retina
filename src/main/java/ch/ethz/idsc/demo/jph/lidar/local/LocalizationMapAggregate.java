// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.slam.OfflineLidarWrap;
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.ScatterImageInvoke;
import ch.ethz.idsc.gokart.offline.slam.WallScatterImage;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum LocalizationMapAggregate {
  ;
  public static void main(String[] args) throws FileNotFoundException, IOException {
    LocalizationConfig localizationConfig = new LocalizationConfig();
    PredefinedMap predefinedMap = localizationConfig.getPredefinedMap();
    BufferedImage image = predefinedMap.getImage();
    BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    {
      Graphics2D graphics2d = bufferedImage.createGraphics();
      graphics2d.setColor(Color.BLACK);
      graphics2d.fillRect(0, 0, image.getWidth(), image.getHeight());
    }
    // BufferedImage background = ImageIO.read(HomeDirectory.Pictures("aggregate.png"));
    ScatterImage scatterImage = //
        new WallScatterImage(bufferedImage, predefinedMap.getModel2Pixel(), new Color(255, 255, 255, 8));
    {
      File file = new File("/media/datahaki/data/gokart/localize/20191022T120450_00/post.lcm");
      ScatterImageInvoke offlineLocalize = new ScatterImageInvoke(scatterImage);
      OfflineLogListener offlineTableSupplier = new OfflineLidarWrap(offlineLocalize);
      OfflineLogPlayer.process(file, offlineTableSupplier);
    }
    {
      File file = new File("/media/datahaki/data/gokart/localize/20191022T120450_01/post.lcm");
      ScatterImageInvoke offlineLocalize = new ScatterImageInvoke(scatterImage);
      OfflineLogListener offlineTableSupplier = new OfflineLidarWrap(offlineLocalize);
      OfflineLogPlayer.process(file, offlineTableSupplier);
    }
    ImageIO.write(scatterImage.getImage(), "png", HomeDirectory.Pictures("aggregate6.png"));
  }
}
