// code by jph
package ch.ethz.idsc.demo.jph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.TensorMap;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

class SpeedClip implements ScalarUnaryOperator {
  private static final Clip CLIP = Clip.function(0, 6);

  @Override
  public Scalar apply(Scalar scalar) {
    return NumberQ.of(scalar) ? CLIP.rescale(scalar) : scalar;
  }
}

public class MacroViewImage {
  private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 13);
  private static final int LENGTH = 600;
  private static final File DEST = UserHome.Pictures("gokartdays");
  private static final int MARGIN_R = 50;
  private static final ScalarUnaryOperator SPEED_CLIP = new SpeedClip();
  private static final ColorDataIndexed COLODATAINDEXED = ColorDataLists._003.cyclic();
  private static final ScalarTensorFunction STF = ColorDataGradients.DENSITY;
  // ---
  private final Tensor table = Array.of(index -> DoubleScalar.INDETERMINATE, LENGTH, 4);

  public MacroViewImage(File daydir) throws Exception {
    String dayname = daydir.getName();
    for (File file : daydir.listFiles()) {
      Tensor tensor = Import.of(file);
      int index = 0;
      for (Tensor row : tensor) {
        if (Scalars.nonZero(row.Get(0)))
          table.set(row, index);
        ++index;
      }
    }
    Tensor img1;
    {
      img1 = Tensors.of(table.get(Tensor.ALL, 2));
      img1 = img1.map(COLODATAINDEXED);
      img1 = ImageResize.nearest(img1, 5, 1);
    }
    Tensor img0;
    {
      img0 = Tensors.of(table.get(Tensor.ALL, 1).map(SPEED_CLIP));
      img0 = img0.map(STF);
      img0 = ImageResize.nearest(img0, 10, 1);
    }
    Tensor image = Join.of(0, img1, img0);
    List<Integer> dims0 = Dimensions.of(image);
    List<Integer> dims1 = Dimensions.of(image);
    dims0.set(1, 75);
    dims1.set(1, MARGIN_R);
    image = Join.of(1, Array.zeros(dims0), image, Array.zeros(dims1));
    {
      BufferedImage bufferedImage = ImageFormat.of(image);
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setFont(FONT);
      graphics.setColor(Color.BLACK);
      graphics.fillRect(0, 0, 1000, 1); // TODO magic const
      graphics.drawString("" + dayname, 0, 13);
      graphics.drawString( //
          String.format("%3d", table.get(Tensor.ALL, 0).stream().filter(NumberQ::of).count()), //
          600 + 75 + MARGIN_R - 32, 13);
      // ---
      image = ImageFormat.from(bufferedImage);
    }
    Export.of(new File(DEST, dayname + ".png"), image);
  }

  public static void main(String[] args) throws Exception {
    DEST.mkdir();
    final int PIY = 12;
    File root = new File("/home/datahaki/gokartproc");
    for (File daydir : root.listFiles())
      if (daydir.isDirectory())
        new MacroViewImage(daydir);
    // ---
    Tensor image = Array.zeros(15, 600 + 75 + MARGIN_R, 4);
    // Tensors.empty();
    List<File> list = new ArrayList<>(Arrays.asList(DEST.listFiles()));
    Collections.sort(list);
    for (File file : list) {
      System.out.println(file);
      image = Join.of(image, Import.of(file));
      // System.out.println(Dimensions.of(image));
    }
    {
      BufferedImage bufferedImage = ImageFormat.of(image);
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      {
        Tensor keyvisual = Import.of(UserHome.file("keyvisual.png"));
        keyvisual = TensorMap.of(rgba -> {
          if (Scalars.isZero(rgba.Get(0)))
            return Tensors.vector(255, 248, 198, 255);
          return rgba;
        }, keyvisual, 2);
        BufferedImage background = ImageFormat.of(keyvisual);
        graphics.drawImage(background, 0, 0, null);
        graphics.drawImage(ImageFormat.of(image), 0, 0, null);
      }
      {
        graphics.setFont(FONT);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString("Date", 0, PIY);
        graphics.drawString("\u03A3[min]", 600 + 75 + 1, PIY);
      }
      for (int hrs = 0; hrs <= 10; ++hrs) {
        int pix = 75 + hrs * 60;
        graphics.setColor(new Color(128, 128, 128, 128));
        graphics.fillRect(pix, 0, 1, bufferedImage.getHeight());
        if (hrs < 10) {
          graphics.setColor(Color.DARK_GRAY);
          graphics.drawString(String.format("%02d:00", 8 + hrs), pix + 1, PIY);
        }
      }
      image = ImageFormat.from(bufferedImage);
    }
    Export.of(UserHome.Pictures("usegokart.png"), image);
  }
}
