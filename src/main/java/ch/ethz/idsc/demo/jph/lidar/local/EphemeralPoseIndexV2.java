// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.StringScalar;

/* package */ enum EphemeralPoseIndexV2 {
  ;
  public static void main(String[] args) throws IOException {
    String suffix = "/dubilab/app/tpqv50";
    File folder = HomeDirectory.file("Projects/ephemeral/src/main/resources" + suffix);
    // ---
    Tensor tensor = Tensors.empty();
    List<File> files = Arrays.asList(folder.listFiles());
    Collections.sort(files);
    for (File file : files) {
      String name = file.getName();
      if (name.endsWith(".csv"))
        tensor.append(StringScalar.of(name.substring(0, name.length() - 4)));
    }
    // ---
    Export.of(new File(folder.getParentFile(), "tpqv50.vector"), tensor);
  }
}
