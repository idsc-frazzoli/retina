// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunTrackVideoWriter {
  ;
  public static void main(String[] args) throws Exception {
    BufferedImage background = ImageIO.read(VideoBackground.IMAGE_FILE);
    String name = "centerline";
    File file = new File("/media/datahaki/data/gokart/ensemble", name + "/log.lcm");
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter( //
        VideoBackground._20190401, //
        background, //
        new TrackVideoConfig(), //
        HomeDirectory.file(name + ".mp4"))) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
    System.out.println("[done.]");
  }
}
