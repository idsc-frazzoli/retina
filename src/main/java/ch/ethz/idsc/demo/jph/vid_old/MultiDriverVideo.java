// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.jph.video.RunVideoBackground;
import ch.ethz.idsc.gokart.offline.video.BasicTrackTable;
import ch.ethz.idsc.gokart.offline.video.VideoBackground;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Round;

public class MultiDriverVideo {
  public MultiDriverVideo(String filename, VideoBackground videoBackground, List<TrackDriving> list) //
      throws IOException, InterruptedException {
    final int max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    BufferedImage bufferedImage = new BufferedImage( //
        videoBackground.dimension().width, //
        videoBackground.dimension().height, //
        BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    try (Mp4AnimationWriter mp4AnimationWriter = new Mp4AnimationWriter(filename, videoBackground.dimension(), 50)) {
      for (int index = 0; index < max; ++index) {
        System.out.println(index);
        Scalar time = list.get(0).timeFor(index);
        graphics.drawImage(videoBackground.bufferedImage, 0, 0, null);
        Tensor model2pixel = videoBackground.model2pixel;
        GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
        // ri.render(geometricLayer, graphics);
        for (TrackDriving trackDriving : list) {
          trackDriving.setRenderIndex(index);
          trackDriving.render(geometricLayer, graphics);
        }
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(String.format("time:%7s[s]", time.map(Round._3)), 0, 25);
        mp4AnimationWriter.append(bufferedImage);
        if (index == 10000)
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
    List<TrackDriving> list = new LinkedList<>();
    for (BasicTrackTable gokartRaceFile : tables(HomeDirectory.file("ensemblelaps/dynamic")))
      list.add(new TrackDriving(gokartRaceFile.tensor, 0));
    for (BasicTrackTable gokartRaceFile : tables(HomeDirectory.file("ensemblelaps/kinematic")))
      list.add(new TrackDriving(gokartRaceFile.tensor, 1));
    for (BasicTrackTable gokartRaceFile : tables(HomeDirectory.file("ensemblelaps/human")))
      list.add(new TrackDriving(gokartRaceFile.tensor, 2));
    for (BasicTrackTable gokartRaceFile : tables(HomeDirectory.file("ensemblelaps/pursuit")))
      list.add(new TrackDriving(gokartRaceFile.tensor, 3));
    VideoBackground videoBackground = RunVideoBackground.get20190414();
    new MultiDriverVideo(HomeDirectory.file("multidriver.mp4").toString(), videoBackground, list);
  }
}
