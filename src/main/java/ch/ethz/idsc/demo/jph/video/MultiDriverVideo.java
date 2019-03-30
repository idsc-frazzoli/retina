// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum MultiDriverVideo {
  ;
  public static void main(String[] args) throws Exception {
    /** Read in some option values and their defaults. */
    final int snaps = 50; // fps
    final String string = "multidriver";
    final String filename = HomeDirectory.file(string + ".mp4").toString();
    // ---
    File src = HomeDirectory.file("track_putty");
    List<TrackDriving> list = new LinkedList<>();
    int id = 0;
    for (File csvFile : Stream.of(src.listFiles()).filter(File::isFile).sorted().collect(Collectors.toList())) {
      TrackDriving trackDriving = new TrackDriving(Import.of(csvFile), id++);
      trackDriving.setDriver(csvFile.getName().substring(0, 2));
      trackDriving.setExtrusion(true);
      list.add(trackDriving);
    }
    BufferedImage background = ImageIO.read(VideoBackground.IMAGE_FILE);
    final int max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    BufferedImage bufferedImage = new BufferedImage( //
        VideoBackground.DIMENSION.width, //
        VideoBackground.DIMENSION.height, //
        BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    try (Mp4AnimationWriter mp4AnimationWriter = new Mp4AnimationWriter(filename, VideoBackground.DIMENSION, snaps)) {
      for (int index = 0; index < max; ++index) {
        System.out.println(index);
        Scalar time = list.get(0).timeFor(index);
        graphics.drawImage(background, 0, 0, null);
        Tensor model2pixel = VideoBackground.MODEL2PIXEL;
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
        if (index == 20000)
          break;
      }
    }
    System.out.println("stopped.");
  }
}
