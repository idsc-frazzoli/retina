// code by jph
package ch.ethz.idsc.gokart.offline.tab;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ch.ethz.idsc.gokart.offline.api.OfflineTableSupplier;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.N;

public class ChannelTimingTable implements OfflineTableSupplier {
  private final TableBuilder tableBuilder = new TableBuilder();
  private final Map<String, Scalar> timings = new TreeMap<>();
  private final Map<String, Tensor> deltas = new TreeMap<>();

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (timings.containsKey(channel)) {
      Scalar last = timings.get(channel);
      deltas.get(channel).append(Magnitude.SECOND.apply(time.subtract(last)));
    } else {
      deltas.put(channel, Tensors.empty());
    }
    timings.put(channel, N.DOUBLE.apply(time));
  }

  @Override
  public Tensor getTable() {
    for (Entry<String, Tensor> entry : deltas.entrySet()) {
      Scalar key = StringScalar.of(entry.getKey());
      Tensor vector = entry.getValue();
      ScalarSummaryStatistics summary = vector.stream() //
          .map(Scalar.class::cast) //
          .collect(ScalarSummaryStatistics.collector());
      Scalar mean = summary.getAverage();
      Scalar var = Variance.ofVector(vector);
      Scalar min = summary.getMin();
      Scalar max = summary.getMax();
      tableBuilder.appendRow( //
          key, //
          RealScalar.of(vector.length()), //
          mean, //
          var, //
          min, //
          max);
    }
    return tableBuilder.toTable();
  }
}
