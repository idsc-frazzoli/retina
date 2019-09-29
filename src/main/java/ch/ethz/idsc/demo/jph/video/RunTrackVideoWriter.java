// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.LastLogMessage;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

// TODO JPH test coverage
/* package */ enum RunTrackVideoWriter {
  ;
  public static void main(String[] args) throws Exception {
    final BackgroundImage backgroundImage = BackgroundImage.from( //
        new File("/media/datahaki/data/gokart/mvb_gnd/20190926T121623_00/background.png"), //
        VideoBackground._20190401);
    final String imageName = "20190926T121623_01";
    final File file = new File("/media/datahaki/data/gokart/mvb_mpc/03/" + imageName, "log.lcm");
    final File dest = HomeDirectory.file(imageName + ".mp4");
    final TrackVideoConfig trackVideoConfig = new TrackVideoConfig();
    trackVideoConfig.frameLimit = 500;
    trackVideoConfig.lidarPoints = 0;
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter(backgroundImage, trackVideoConfig, dest)) {
      {
        File mapFile = new File("/media/datahaki/data/gokart/mvb_map/20190926T121623_00/log.lcm");
        String channel = GokartLcmChannel.XYR_TRACK_CLOSED;
        Optional<ByteBuffer> optional = LastLogMessage.of(mapFile, channel);
        if (optional.isPresent()) {
          System.out.println("bspline track");
          trackVideoWriter.event(null, channel, optional.get());
        }
      }
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
    System.out.println("[done.]");
  }
}
