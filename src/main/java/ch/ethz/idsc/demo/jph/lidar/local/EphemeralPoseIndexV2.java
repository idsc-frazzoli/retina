// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.StringScalar;

/** produces the file "tpqv50.vector" that lists all csv files of pose tables V2 */
/* package */ enum EphemeralPoseIndexV2 {
  ;
  public static void main(String[] args) throws IOException {
    String suffix = GokartPoseDataV2.PATH_FOLDER;
    File folder = HomeDirectory.file("Projects/ephemeral/src/main/resources" + suffix);
    // ---
    List<File> files = Arrays.asList(folder.listFiles());
    Collections.sort(files);
    Tensor tensor = Tensors.reserve(files.size());
    for (File file : files) {
      String name = file.getName();
      if (name.endsWith(".csv"))
        tensor.append(StringScalar.of(name.substring(0, name.length() - 4)));
    }
    // ---
    Export.of(new File(folder.getParentFile(), "tpqv50.vector"), tensor);
  }
}
