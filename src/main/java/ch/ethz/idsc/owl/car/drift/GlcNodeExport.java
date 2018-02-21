// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.sca.N;

class GlcNodeExport {
  private final Tensor table = Tensors.empty();

  /** @param header for instance "TIME,X,Y,THETA,U_OMEGA,U_VEL" */
  public GlcNodeExport(String header) {
    String[] entries = header.split(",");
    table.append(Tensors.vector(i -> StringScalar.of(entries[i].trim()), entries.length));
  }

  public void append(GlcNode node) {
    Tensor t = Tensors.of(node.stateTime().time());
    Tensor x = node.stateTime().state();
    Tensor u = node.isRoot() ? //
        Array.zeros(table.get(0).length() - x.length() - 1) : //
        node.flow().getU();
    Tensor row = Join.of(t, x, u);
    GlobalAssert.that(row.length() == table.get(0).length());
    table.append(N.DOUBLE.of(row));
  }

  public void writeToFile(File file) throws IOException {
    Export.of(file, table);
  }
}
