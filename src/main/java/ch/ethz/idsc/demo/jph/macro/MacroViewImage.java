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
import ch.ethz.idsc.tensor.NumberQ;
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
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** generates an image that lists the operation of the gokart
 * 
 * https://user-images.githubusercontent.com/4012178/44048221-08391a08-9f31-11e8-86ec-df450f4051e6.png */
/* package */ class MacroViewImage {
  private static final int FONT_SIZE = 11;
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, FONT_SIZE);
  private static final int LENGTH = MacroViewTable.LENGTH;
  private static final int MARGIN_TOP = 12;
  private static final int MARGIN_L = 65;
  private static final int MARGIN_R = 40;
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._003.cyclic();
  private static final ScalarTensorFunction GRADIENT = ColorDataGradients.AVOCADO;
  private static final TensorUnaryOperator BLACK_TO_YELLOW = rgba -> {
    if (Scalars.isZero(rgba.Get(0)))
      return Tensors.vector(255, 248, 198, 255);
    return rgba;
  };
  // ---
  private final Tensor row;
  private final int minutes;

  public MacroViewImage(File daydir) throws Exception {
    Tensor table = Array.of(index -> DoubleScalar.INDETERMINATE, LENGTH, 4);
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
      img1 = img1.map(COLOR_DATA_INDEXED);
      img1 = ImageResize.nearest(img1, 5, 1); // autonomous bar
    }
    Tensor img0;
    {
      img0 = Tensors.of(table.get(Tensor.ALL, 1).map(SpeedClip.FUNCTION));
      img0 = img0.map(GRADIENT);
      img0 = ImageResize.nearest(img0, 6, 1); // speed bar
    }
    Tensor image = Join.of(0, img1, img0);
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
      minutes = (int) table.get(Tensor.ALL, 0).stream().filter(NumberQ::of).count();
      FontMetrics fontMetrics = graphics.getFontMetrics();
      String string = String.format("%3d", minutes);
      int width = fontMetrics.stringWidth(string);
      graphics.drawString(string, width() - width, FONT_SIZE - 1);
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
        .collect(Collectors.toList());
    Tensor image = Array.zeros(MARGIN_TOP, width(), 4);
    List<MacroViewImage> rows = new LinkedList<>();
    for (File daydir : list) {
      MacroViewImage macroViewImage = new MacroViewImage(daydir);
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
      {
        graphics.setFont(FONT);
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString("Date", 0, piy);
        graphics.drawString("\u03A3[min]", MARGIN_L + LENGTH + 1, piy);
      }
      final int last = LENGTH / 60;
      for (int hrs = 0; hrs <= last; ++hrs) {
        int pix = MARGIN_L + hrs * 60;
        graphics.setColor(new Color(128, 128, 128, 128));
        graphics.fillRect(pix, 0, 1, bufferedImage.getHeight());
        if (hrs < last) {
          graphics.setColor(Color.DARK_GRAY);
          graphics.drawString(String.format("%02d:00", 8 + hrs), pix + 1, piy);
        }
      }
      image = ImageFormat.from(bufferedImage);
    }
    Export.of(HomeDirectory.Pictures("gokart_operation.png"), image);
  }
}
