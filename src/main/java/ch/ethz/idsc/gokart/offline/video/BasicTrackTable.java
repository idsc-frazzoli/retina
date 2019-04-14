// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.tab.BasicTrackReplayTable;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.sca.N;

/** class helps to compare the performance of the gokart in across log files */
public class BasicTrackTable implements Comparable<BasicTrackTable> {
  public static BasicTrackTable from(File file) {
    BasicTrackReplayTable basicTrackReplayTable = new BasicTrackReplayTable();
    try {
      OfflineLogPlayer.process(file, basicTrackReplayTable);
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException();
    }
    return new BasicTrackTable(file, basicTrackReplayTable.getTable().unmodifiable());
  }

  // ---
  public final File file;
  public final Tensor tensor;
  private final Scalar duration;

  public BasicTrackTable(File file, Tensor tensor) {
    this.file = file;
    this.tensor = tensor;
    duration = Last.of(tensor).Get(0);
  }

  @Override // from Comparable
  public int compareTo(BasicTrackTable gokartRaceFile) {
    return Scalars.compare(duration, gokartRaceFile.duration);
  }

  public Scalar duration() {
    return N.DOUBLE.of(duration);
  }
}
