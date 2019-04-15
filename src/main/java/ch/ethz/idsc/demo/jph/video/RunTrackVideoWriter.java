// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunTrackVideoWriter {
  ;
  public static void main(String[] args) throws Exception {
    File file = new File("/home/datahaki/ensemblelaps/dynamic/m13.lcm");
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter( //
        RunVideoBackground.get20190414(), //
        new TrackVideoConfig(), //
        HomeDirectory.file("dynamic13.mp4"))) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
    System.out.println("[done.]");
  }
}
