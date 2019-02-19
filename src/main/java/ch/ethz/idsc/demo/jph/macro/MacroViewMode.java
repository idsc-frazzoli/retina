// code by jph
package ch.ethz.idsc.demo.jph.macro;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.alg.TensorMap;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.red.Total;

/** generates an image that lists the operation of the gokart
 * 
 * https://user-images.githubusercontent.com/4012178/44048221-08391a08-9f31-11e8-86ec-df450f4051e6.png */
/* package */ class MacroViewMode {
  private static final int FONT_SIZE = 11;
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, FONT_SIZE);
  private static final int LENGTH = 400;
  private static final int MARGIN_TOP = 12;
  private static final int MARGIN_L = 65;
  private static final int MARGIN_R = 40;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._003.cyclic();
  private static final ScalarTensorFunction GRADIENT = ColorDataGradients.SOLAR;
  private static final TensorUnaryOperator BLACK_TO_YELLOW = rgba -> {
    if (Scalars.isZero(rgba.Get(0)))
      return Tensors.vector(255, 248, 198, 255);
    return rgba;
  };
  // ---
  private final Tensor row;

  public MacroViewMode(File daydir) throws Exception {
    Tensor table = Array.of(index -> DoubleScalar.of(0), MacroViewTable.LENGTH, 4);
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
    final Scalar autonomous = Total.of(table.get(Tensor.ALL, 2)).Get(); // minutes of autonomous driving
    Scalar minSpeed = RealScalar.of(0.5);
    final Scalar manual = Total.of(table.get(Tensor.ALL, 1).map(speed -> Boole.of(Scalars.lessEquals(minSpeed, speed)))).subtract(autonomous).Get();
    Tensor img1 = Tensors.of(Range.of(0, 200).map(s -> Scalars.lessEquals(s, autonomous) ? RealScalar.ONE : DoubleScalar.INDETERMINATE));
    img1 = img1.map(COLOR_DATA_INDEXED); // autonomous bar
    Tensor img0 = Tensors.of(Reverse.of(Range.of(0, 200).map(s -> Scalars.lessEquals(s, manual) ? RealScalar.ONE : DoubleScalar.INDETERMINATE)));
    img0 = img0.map(GRADIENT); // speed bar
    // ---
    Tensor image = ImageResize.nearest(Join.of(1, img0, img1), 11, 1);
    List<Integer> dims0 = Dimensions.of(image);
    List<Integer> dims1 = Dimensions.of(image);
    dims0.set(1, MARGIN_L);
    dims1.set(1, MARGIN_R);
    image = Join.of(1, Array.zeros(dims0), image, Array.zeros(dims1));
    {
      BufferedImage bufferedImage = ImageFormat.of(image);
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setFont(FONT);
      graphics.setColor(Color.DARK_GRAY);
      graphics.fillRect(0, 0, width(), 1);
      graphics.drawString(dayname, 0, FONT_SIZE - 1);
      // ---
      image = ImageFormat.from(bufferedImage);
    }
    row = image;
  }

  private static int width() {
    return MARGIN_L + LENGTH + MARGIN_R;
  }

  public static void main(String[] args) throws Exception {
    List<File> list = Stream.of(MacroViewTable.ROOT.listFiles()) //
        .filter(File::isDirectory) //
        .sorted() //
        .limit(10) //
        .collect(Collectors.toList());
    Tensor image = Array.zeros(MARGIN_TOP, width(), 4);
    List<MacroViewMode> rows = new LinkedList<>();
    for (File daydir : list) {
      MacroViewMode macroViewImage = new MacroViewMode(daydir);
      image = Join.of(image, macroViewImage.row);
      rows.add(macroViewImage);
    }
    // ---
    {
      BufferedImage bufferedImage = ImageFormat.of(image);
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      {
        Tensor keyvisual = ResourceData.of("/eth/marketing/keyvisual.png");
        keyvisual = TensorMap.of(BLACK_TO_YELLOW, keyvisual, 2);
        BufferedImage background = ImageFormat.of(keyvisual);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        graphics.drawImage(background, 0, 0, null);
        graphics.drawImage(background, 0, background.getHeight(), null);
        graphics.drawImage(ImageFormat.of(image), 0, 0, null);
      }
      final int piy = MARGIN_TOP - 1;
      final int last = LENGTH / 60;
      for (int hrs = -3; hrs <= 3; ++hrs) {
        int pix = MARGIN_L + 200 + hrs * 60;
        graphics.setColor(new Color(128, 128, 128, 128));
        graphics.fillRect(pix, 0, 1, bufferedImage.getHeight());
        if (hrs < last) {
          graphics.setColor(Color.DARK_GRAY);
          graphics.drawString(String.format("%02d:00", Math.abs(hrs)), pix + 1, piy);
        }
      }
      {
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font(Font.DIALOG, Font.BOLD, FONT_SIZE));
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int stringWidth = fontMetrics.stringWidth("manual");
        graphics.drawString("manual", MARGIN_L + LENGTH / 2 - stringWidth - 5, piy + MARGIN_TOP - 1);
        graphics.drawString("autonomous", MARGIN_L + LENGTH / 2 + 5, piy + MARGIN_TOP - 1);
      }
      image = ImageFormat.from(bufferedImage);
    }
    Export.of(HomeDirectory.Pictures("gokart_mode.png"), image);
  }
}
