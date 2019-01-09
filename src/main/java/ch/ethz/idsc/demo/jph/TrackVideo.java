// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum TrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    /** Read in some option values and their defaults. */
    final int snaps = 20; // fps
    final String filename = HomeDirectory.file("filename3.mp4").toString();
    Dimension dimension = new Dimension(1920, 1080);
    // ---
    File folder = new File("/media/datahaki/media/ethz/gokart/topic/track_red");
    File src = HomeDirectory.file("track_r");
    List<TrackDriving> list = new LinkedList<>();
    int id = 0;
    for (File file : folder.listFiles()) {
      GokartLogInterface gokartLogInterface = GokartLogAdapter.of(file);
      String title = file.getName();
      File csvFile = new File(src, title + ".csv");
      if (csvFile.isFile()) {
        TrackDriving trackDriving = new TrackDriving(Import.of(csvFile), id++);
        trackDriving.setDriver(gokartLogInterface.driver());
        System.out.println(trackDriving.row(0));
        list.add(trackDriving);
      }
    }
    int max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    // max = 500;
    BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    ImageRegion imageRegion = LocalizationConfig.getPredefinedMap().getImageRegion();
    RenderInterface imageRender = RegionRenders.create(imageRegion);
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(filename, dimension, snaps)) {
      for (int index = 0; index < max; ++index) {
        // System.out.println(index);
        Scalar time = list.get(0).timeFor(index);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, dimension.width, dimension.height);
        Tensor model2pixel = Tensors.fromString( //
            "{{42.82962839003549, 42.01931617686636, -2924.9980200038317}, {42.01931617686636, -42.8296, 575.4392}, {0, 0, 1}}");
        GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
        imageRender.render(geometricLayer, graphics);
        for (TrackDriving trackDriving : list) {
          trackDriving.setRenderIndex(index);
          trackDriving.render(geometricLayer, graphics);
        }
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(String.format("time:%7s[s]", time.map(Round._3)), 0, 25);
        mp4.append(bufferedImage);
      }
    }
    System.out.println("stopped.");
  }
}
