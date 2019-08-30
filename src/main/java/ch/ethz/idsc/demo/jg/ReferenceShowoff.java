// coed by gjoel
package ch.ethz.idsc.demo.jg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.VideoBackground;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ enum ReferenceShowoff {
  ;
  private static final Stroke STROKE = new BasicStroke(1.5f);
  private static final Color COLOR = Color.RED;

  public static void render(File directory) throws IOException {
    File image = new File(directory, directory.getName() + ".bck.png");
    if (!image.isFile()) {
      image = VideoBackground.render(directory);
      System.out.println(" background image " + image.getName());
    }
    BufferedImage bufferedImage = ImageIO.read(image);
    Graphics2D graphics = bufferedImage.createGraphics();
    // ---
    OfflineLogListener offlineLogListener = new OfflineLogListener() {
      final PathRender pathRender = new PathRender(COLOR, STROKE);
      final GeometricLayer geometricLayer = new GeometricLayer(VideoBackground._20190401, Array.zeros(3));

      @Override
      public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
        if (channel.equals(GokartLcmChannel.PURSUIT_CURVE_SE2)) {
          Tensor reference = Se2CurveLcm.decode(byteBuffer).unmodifiable();
          pathRender.setCurve(reference, true);
          pathRender.render(geometricLayer, graphics);
        }
      }
    };
    // ---
    System.out.print("processing... ");
    OfflineLogPlayer.process(GokartLogAdapter.of(directory).file(), offlineLogListener);
    System.out.println("finished");
    ImageIO.write(bufferedImage, "png", new File(directory, directory.getName() + ".ref.png"));
  }

  public static void main(String[] args) throws IOException {
    Optional<File> optionalFile = FileHelper.open(args);
    if (optionalFile.isPresent()) {
      File file = optionalFile.get();
      try {
        render(file);
      } catch (FileNotFoundException e) {
        // most common error is to select file instead of directory
        render(file.getParentFile());
      }
    }
  }
}
