// code by jph
package ch.ethz.idsc.demo.jph.vid_old;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionRender;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionSequenceRender;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum TrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    System.out.print("building index...");
    NavigableMap<Scalar, ControlAndPredictionSteps> navigableMap = //
        ControlAndPredictionStepsIndex.build(TrackDrivingTables.SINGLETON);
    System.out.println("done");
    /** Read in some option values and their defaults. */
    final int snaps = 50; // fps
    final String string = TrackDrivingTables.SINGLETON.getParentFile().getName();
    final String filename = HomeDirectory.file(string + ".mp4").toString();
    // ---
    // File src = HomeDirectory.file("track_putty");
    List<TrackDriving> list = new LinkedList<>();
    int id = 0;
    File csvFile = HomeDirectory.file("track_putty", string + ".csv");
    if (csvFile.isFile()) {
      TrackDriving trackDriving = new TrackDriving(Import.of(csvFile), id++);
      trackDriving.setDriver(csvFile.getName().startsWith("mh") ? "mh" : "tg");
      trackDriving.setExtrusion(true);
      // System.out.println(trackDriving.row(0));
      list.add(trackDriving);
    }
    BackgroundImage backgroundImage = VideoBackground.get20190414();
    final int max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    BufferedImage bufferedImage = new BufferedImage( //
        backgroundImage.dimension().width, //
        backgroundImage.dimension().height, //
        BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    // PathRender pathRender = new PathRender(new Color(115, 167, 115, 64),
    // new BasicStroke(6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f));
    // Tensor optimal = Import.of(new File(src, "opt/onelap.csv"));
    // RenderInterface ri = pathRender.setCurve(optimal, true);
    MPCPredictionSequenceRender mpcPredictionSequenceRender = new MPCPredictionSequenceRender(30);
    try (Mp4AnimationWriter mp4AnimationWriter = new Mp4AnimationWriter(filename, backgroundImage.dimension(), snaps)) {
      for (int index = 0; index < max; ++index) {
        System.out.println(index);
        Scalar time = list.get(0).timeFor(index);
        graphics.drawImage(backgroundImage.bufferedImage(), 0, 0, null);
        GeometricLayer geometricLayer = GeometricLayer.of(backgroundImage.model2pixel());
        // ri.render(geometricLayer, graphics);
        {
          Entry<Scalar, ControlAndPredictionSteps> floorEntry = navigableMap.floorEntry(Quantity.of(time, SI.SECOND));
          if (Objects.nonNull(floorEntry)) {
            ControlAndPredictionSteps controlAndPredictionSteps = floorEntry.getValue();
            mpcPredictionSequenceRender.getControlAndPredictionSteps(controlAndPredictionSteps);
            mpcPredictionSequenceRender.render(geometricLayer, graphics);
            MPCPredictionRender mpcPredictionRender = new MPCPredictionRender();
            mpcPredictionRender.getControlAndPredictionSteps(controlAndPredictionSteps);
            mpcPredictionRender.render(geometricLayer, graphics);
          }
        }
        for (TrackDriving trackDriving : list)
          trackDriving.render(index, geometricLayer, graphics);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(String.format("time:%7s[s]", time.map(Round._3)), 0, 25);
        mp4AnimationWriter.append(bufferedImage);
        if (index == 200)
          break;
      }
    }
    System.out.println("stopped.");
  }
}
