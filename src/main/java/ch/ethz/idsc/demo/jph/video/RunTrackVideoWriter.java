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
        RunVideoBackground.get20190527(), new TrackVideoConfig(), dest)) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
  }

  public static void main(String[] args) throws Exception {
    run(new File("/media/datahaki/data/gokart/davis240c/20190527/20190527T161637_02/log.lcm"), //
        HomeDirectory.file("manual.mp4"));
    System.out.println("[done.]");
  }
}
