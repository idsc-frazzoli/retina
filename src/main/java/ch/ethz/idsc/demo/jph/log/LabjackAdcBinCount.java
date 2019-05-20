// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.gokart.offline.channel.LabjackAdcChannel;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.BinCounts;

/* package */ class LabjackAdcBinCount implements OfflineTableSupplier {
  private static final Scalar _1 = DoubleScalar.of(1);
  private static final Scalar BINSIZE = RationalScalar.of(1, 8);
  private static final TensorUnaryOperator PADRIGHT = PadRight.zeros(8 * (15 + 1));
  // ---
  private final TableBuilder tableBuilder = new TableBuilder();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(LabjackAdcChannel.INSTANCE.channel())) {
      LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(byteBuffer);
      tableBuilder.appendRow(labjackAdcFrame.asVector().map(_1::add));
    }
  }

  @Override
  public Tensor getTable() {
    Tensor tensor = tableBuilder.toTable().copy();
    return Tensor.of(IntStream.range(0, 5) //
        .mapToObj(i -> PADRIGHT.apply(BinCounts.of(tensor.get(Tensor.ALL, i), BINSIZE))));
  }

  public static void main(String[] args) {
    for (GokartLogFile gokartLogFile : GokartLogFile.values())
      if (GokartLogFile._20181213T100655_add1a7bf.ordinal() <= gokartLogFile.ordinal()) {
        File export = HomeDirectory.Documents("labjack", gokartLogFile.getTitle() + ".csv");
        if (!export.exists())
          try {
            System.out.println(gokartLogFile);
            File file = DatahakiLogFileLocator.file(gokartLogFile);
            LabjackAdcBinCount gokartAdc = new LabjackAdcBinCount();
            OfflineLogPlayer.process(file, gokartAdc);
            Tensor tensor = gokartAdc.getTable();
            Export.of(export, tensor);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
      }
  }
}
