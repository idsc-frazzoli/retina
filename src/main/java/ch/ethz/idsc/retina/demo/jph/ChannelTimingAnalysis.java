// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TensorBuilder;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Variance;
import ch.ethz.idsc.tensor.sca.N;

class ChannelTimingAnalysis implements OfflineTableSupplier {
  private final TensorBuilder tensorBuilder = new TensorBuilder();
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
      Scalar mean = Mean.of(vector).Get();
      Scalar var = Variance.ofVector(vector);
      Scalar min = vector.stream().reduce(Min::of).get().Get();
      Scalar max = vector.stream().reduce(Max::of).get().Get();
      tensorBuilder.append(Tensors.of( //
          key, //
          RealScalar.of(vector.length()), //
          mean, //
          var, //
          min, //
          max));
    }
    return tensorBuilder.getTensor();
  }

  public static void main(String[] args) throws IOException {
    OfflineProcessing.handle(() -> new ChannelTimingAnalysis());
  }
}
