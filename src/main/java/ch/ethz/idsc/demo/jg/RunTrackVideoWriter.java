// code by jph
package ch.ethz.idsc.demo.jg;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.video.TrackVideoConfig;
import ch.ethz.idsc.gokart.offline.video.TrackVideoWriter;

/* package */ enum RunTrackVideoWriter {
  ;
  private static void run(File file, File dest) throws IOException, Exception {
    try (TrackVideoWriter trackVideoWriter = new TrackVideoWriter( //
        RunVideoBackground.get20190606(), new TrackVideoConfig(), dest)) {
      OfflineLogPlayer.process(file, trackVideoWriter);
    }
  }

  public static void main(String[] args) throws Exception {
    Optional<File> optional = FileHelper.open(args);
    if (optional.isPresent()) {
      File file = optional.get();
      System.out.println(file.getAbsolutePath());
      String name = file.getName().endsWith(".00") ? file.getName().split("_")[0] : file.getParentFile().getName();
      run(file, new File(file.getParentFile(), name + ".mp4"));
      System.out.println("[done.]");
    }
  }
}
