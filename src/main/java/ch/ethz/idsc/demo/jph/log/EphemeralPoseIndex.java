// code by jph
package ch.ethz.idsc.demo.jph.log;

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

/* package */ enum EphemeralPoseIndex {
  ;
  public static void main(String[] args) throws IOException {
    File root = HomeDirectory.file("Projects/ephemeral/src/main/resources/dubilab/app/pose");
    List<File> folders = Arrays.asList(root.listFiles());
    Collections.sort(folders);
    // List<String> output = new LinkedList<>();
    Tensor tensor = Tensors.empty();
    for (File folder : folders)
      if (folder.isDirectory()) {
        List<File> files = Arrays.asList(folder.listFiles());
        Collections.sort(files);
        for (File file : files) {
          String name = file.getName();
          if (name.endsWith(".csv"))
            tensor.append(StringScalar.of(folder.getName() + "/" + name.substring(0, name.length() - 4)));
        }
      }
    // ---
    Export.of(new File(root, "index.vector"), tensor);
  }
}
