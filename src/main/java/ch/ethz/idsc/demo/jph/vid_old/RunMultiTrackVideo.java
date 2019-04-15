// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.ethz.idsc.demo.jph.video.RunVideoBackground;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.BasicTrackTable;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/**/ enum RunMultiTrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    Map<ControlType, List<TrackDriving>> map = new EnumMap<>(ControlType.class);
    for (ControlType controlType : ControlType.values()) {
      map.put(controlType, new ArrayList<>());
      for (BasicTrackTable gokartRaceFile : MultiTrackVideo.tables(HomeDirectory.file("ensemblelaps", controlType.name().toLowerCase())))
        map.get(controlType).add(new TrackDriving(gokartRaceFile.tensor, controlType.ordinal()));
    }
    BackgroundImage backgroundImage = RunVideoBackground.get20190414();
    ControlComparisonRender controlComparisonRender = new ControlComparisonRender(map);
    new MultiTrackVideo(HomeDirectory.file("multidriver.mp4").toString(), backgroundImage, //
        map.values().stream().flatMap(List::stream).collect(Collectors.toList()), //
        controlComparisonRender);
  }
}
