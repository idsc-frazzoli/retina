// code by jph
package ch.ethz.idsc.demo.jph.lidar.local;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum EphemeralGokartPoseV2 {
  ;
  public static void main(String[] args) throws IOException {
    final File dest = HomeDirectory.file("Projects/ephemeral/src/main/resources/dubilab/app/pv50");
    File root = new File("/media/datahaki/data/gokart/pose50");
    if (dest.isDirectory())
      for (File date : root.listFiles())
        if (date.isDirectory())
          for (File logfolder : date.listFiles()) {
            File csvdir = new File(logfolder, "csv");
            if (csvdir.isDirectory()) {
              File file = new File(csvdir, "gokart.pose.lidar.csv.gz");
              Tensor tensor = Import.of(file);
              String name = logfolder.getName();
              File write = new File(dest, name + ".csv");
              Export.of(write, tensor);
              Tensor imprt = Import.of(write);
              Chop.NONE.requireClose(imprt, tensor);
              if (StringScalarQ.any(imprt))
                throw TensorRuntimeException.of(imprt);
            } else
              System.err.println("skip " + csvdir);
          }
  }
}
