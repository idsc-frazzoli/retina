// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

// TODO JPH test coverage
/* package */ enum RunTrackVideoWriter {
  ;
  public static void main(String[] args) throws Exception {
    String imageName = "20190701T175650_00";
    BackgroundImage backgroundImage = BackgroundImage.from( //
        HomeDirectory.Pictures(imageName + ".png"), //
        VideoBackground._20190401);
    File file = new File("/media/datahaki/data/gokart/0701mpc/20190701T175650_01/log.lcm");
    File dest = HomeDirectory.file(imageName + ".mp4");
    TrackVideoConfig trackVideoConfig = new TrackVideoConfig();
    // trackVideoConfig.frameLimit = 500;
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter(backgroundImage, trackVideoConfig, dest)) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
    System.out.println("[done.]");
  }
}
