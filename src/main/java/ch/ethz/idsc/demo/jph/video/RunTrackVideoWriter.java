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
    String name = "20190401T115537_00";
    File file = new File("/media/datahaki/data/gokart/cuts/20190401", name + "/log.lcm");
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter( //
        RunVideoBackground.get20190414(), //
        new TrackVideoConfig(), //
        HomeDirectory.file(name + ".mp4"))) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
    System.out.println("[done.]");
  }
}
