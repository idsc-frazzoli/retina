// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.offline.slam.ObstacleAggregate;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum TrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    /** Read in some option values and their defaults. */
    final int snaps = 50; // fps
    final String filename = HomeDirectory.file("filename3.mp4").toString();
    Dimension dimension = new Dimension(1920, 1080);
    // ---
    // File folder = new File("/media/datahaki/media/ethz/gokart/topic/track_red");
    File src = HomeDirectory.file("track_putty");
    List<TrackDriving> list = new LinkedList<>();
    int id = 0;
    for (File csvFile : src.listFiles())
      if (csvFile.isFile()) {
        // GokartLogInterface gokartLogInterface = GokartLogAdapter.of(file);
        // String title = file.getName();
        // File csvFile = new File(file);
        // if (csvFile.isFile())
        {
          TrackDriving trackDriving = new TrackDriving(Import.of(csvFile), id++);
          trackDriving.setDriver(csvFile.getName().startsWith("r") ? "tg" : "mh");
          // System.out.println(trackDriving.row(0));
          list.add(trackDriving);
        }
      }
    BufferedImage background = ImageIO.read(HomeDirectory.file("20190318T142605_05.png"));
    int max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    // max = 500;
    BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    PathRender pathRender = new PathRender(new Color(64, 255, 64, 128),
        new BasicStroke(6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f));
    Tensor optimal = Import.of(new File(src, "opt/onelap.csv"));
    RenderInterface ri = pathRender.setCurve(optimal, true);
    // ImageRegion imageRegion = LocalizationConfig.getPredefinedMap().getImageRegion();
    // RenderInterface imageRender = RegionRenders.create(imageRegion);
    try (Mp4AnimationWriter mp4 = new Mp4AnimationWriter(filename, dimension, snaps)) {
      for (int index = 0; index < max; ++index) {
        System.out.println(index);
        Scalar time = list.get(0).timeFor(index);
        graphics.setColor(Color.WHITE);
        // graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.drawImage(background, 0, 0, null);
        Tensor model2pixel = ObstacleAggregate.MODEL2PIXEL;
        // Tensors.fromString( //
        // "{{42.82962839003549, 42.01931617686636, -2924.9980200038317}, {42.01931617686636, -42.8296, 575.4392}, {0, 0, 1}}");
        GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
        ri.render(geometricLayer, graphics);
        // imageRender.render(geometricLayer, graphics);
        for (TrackDriving trackDriving : list) {
          trackDriving.setRenderIndex(index);
          trackDriving.render(geometricLayer, graphics);
        }
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(String.format("time:%7s[s]", time.map(Round._3)), 0, 25);
        mp4.append(bufferedImage);
        if (index == 10000)
          break;
      }
    }
    System.out.println("stopped.");
  }
}
