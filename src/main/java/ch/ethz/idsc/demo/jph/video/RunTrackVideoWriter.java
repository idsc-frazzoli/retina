// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.map.DubendorfFrame;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.LastLogMessage;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunTrackVideoWriter {
  ;
  public static void main(String[] args) throws Exception {
    final BackgroundImage backgroundImage = BackgroundImage.from( //
        new File("/media/datahaki/data/gokart/mvb_gnd/20190926T121623_00/background.png"), //
        DubendorfFrame._20190401);
    File folder = new File("/media/datahaki/data/gokart/mvb_mpc/03");
    for (File id : folder.listFiles()) {
      final String imageName = id.getName();
      System.out.println(imageName);
      final File file = new File("/media/datahaki/data/gokart/mvb_mpc/03/" + imageName, "log.lcm");
      final File dest = HomeDirectory.file("video", imageName + ".mp4");
      final TrackVideoConfig trackVideoConfig = new TrackVideoConfig();
      // trackVideoConfig.frameLimit = 1000;
      trackVideoConfig.lidarPoints = 0;
      try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter(backgroundImage, trackVideoConfig, dest)) {
        {
          File mapFile = new File("/media/datahaki/data/gokart/mvb_map/20190926T121623_00/log.lcm");
          String channel = GokartLcmChannel.XYR_TRACK_CYCLIC;
          Optional<ByteBuffer> optional = LastLogMessage.of(mapFile, channel);
          if (optional.isPresent()) {
            System.out.println("bspline track");
            trackVideoWriter.event(null, channel, optional.get());
          }
        }
        OfflineLogPlayer.process(file, trackVideoWriter);
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      }
      System.out.println("[done.]");
    }
  }
}
