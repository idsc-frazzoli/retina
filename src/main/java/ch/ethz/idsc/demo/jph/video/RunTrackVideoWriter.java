// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum RunTrackVideoWriter {
  ;
  private static void run(File file, File dest) throws IOException, Exception {
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter( //
        RunVideoBackground.get20190530(), new TrackVideoConfig(), dest)) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
  }

  public static void main(String[] args) throws Exception {
    run(new File("/media/datahaki/data/gokart/plans/20190530/20190530T143412_00/log.lcm"), //
        HomeDirectory.file("clothoid_plans.mp4"));
    System.out.println("[done.]");
  }
}
