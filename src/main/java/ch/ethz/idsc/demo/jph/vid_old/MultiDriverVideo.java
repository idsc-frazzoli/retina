// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.jph.video.RunVideoBackground;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.gokart.offline.video.BasicTrackTable;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;

public class MultiDriverVideo {
  private final int max;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;

  public MultiDriverVideo(String filename, BackgroundImage backgroundImage, List<TrackDriving> list, AbstractFrameRender... abstractFrameRenders) //
      throws IOException, InterruptedException {
    max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    bufferedImage = new BufferedImage( //
        backgroundImage.dimension().width, //
        backgroundImage.dimension().height, //
        BufferedImage.TYPE_3BYTE_BGR);
    graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    try (Mp4AnimationWriter mp4AnimationWriter = new Mp4AnimationWriter(filename, backgroundImage.dimension(), 50)) {
      for (int index = 0; index < max; ++index) {
        System.out.println(index);
        // draw background image
        graphics.drawImage(backgroundImage.bufferedImage, 0, 0, null);
        // draw all vehicles
        Tensor model2pixel = backgroundImage.model2pixel;
        GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
        for (TrackDriving trackDriving : list)
          trackDriving.render(index, geometricLayer, graphics);
        for (AbstractFrameRender abstractFrameRender : abstractFrameRenders)
          abstractFrameRender.render(index, geometricLayer, graphics);
        mp4AnimationWriter.append(bufferedImage);
        if (index == 2000)
          break;
      }
    }
    System.out.println("stopped.");
  }

  public static List<BasicTrackTable> tables(File folder) {
    return Stream.of(folder.listFiles()) //
        .filter(File::isFile) //
        .map(BasicTrackTable::from) //
        .sorted() //
        .collect(Collectors.toList());
  }

  public static void main(String[] args) throws Exception {
    Map<ControlType, List<TrackDriving>> map = new EnumMap<>(ControlType.class);
    for (ControlType controlType : ControlType.values()) {
      map.put(controlType, new ArrayList<>());
      for (BasicTrackTable gokartRaceFile : tables(HomeDirectory.file("ensemblelaps", controlType.name().toLowerCase())))
        map.get(controlType).add(new TrackDriving(gokartRaceFile.tensor, controlType.ordinal()));
    }
    BackgroundImage backgroundImage = RunVideoBackground.get20190414();
    ControlComparisonRender controlComparisonRender = new ControlComparisonRender(map);
    new MultiDriverVideo(HomeDirectory.file("multidriver.mp4").toString(), backgroundImage, //
        map.values().stream().flatMap(List::stream).collect(Collectors.toList()), //
        controlComparisonRender);
  }
}
