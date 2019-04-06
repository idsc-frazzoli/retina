package ch.ethz.idsc.demo.jph.video;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.video.TrackVideoRender;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunTrackVideoRender {
  ;
  public static void main(String[] args) throws Exception {
    BufferedImage background = ImageIO.read(VideoBackground.IMAGE_FILE);
    String name = "20190401T115537_00";
    File file = new File("/media/datahaki/data/gokart/cuts/20190401", name + "/log.lcm");
    try (TrackVideoRender trackVideoRender = new TrackVideoRender( //
        VideoBackground._20190401, //
        background, //
        GokartPoseChannel.INSTANCE.channel(), //
        HomeDirectory.file(name + ".mp4"))) {
      OfflineLogPlayer.process(file, trackVideoRender);
    }
    System.out.println("[done.]");
  }
}
