// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.slam.ObstacleAggregate;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum ObstacleMapImage {
  ;
  public static void main(String[] args) throws IOException {
    GokartLogInterface gokartLogInterface = //
        GokartLogAdapter.of(new File("/media/datahaki/data/gokart/cuts/20190329/20190329T144049_00"));
    PredefinedMap predefinedMap = LocalizationConfig.GLOBAL.getPredefinedMap();
    BufferedImage bufferedImage = predefinedMap.getImage();
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(255, 255, 255, 8));
    ObstacleAggregate obstacleAggregate = new ObstacleAggregate( //
        GokartPoseChannel.INSTANCE.channel(), //
        graphics, //
        predefinedMap.getModel2Pixel(), //
        gokartLogInterface.pose());
    OfflineLogPlayer.process(gokartLogInterface.file(), obstacleAggregate);
    // ;
    ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("20190314.png"));
  }
}
