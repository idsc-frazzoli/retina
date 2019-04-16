// code by jph
package ch.ethz.idsc.gokart.calib.brake;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Clip;

/* package */ enum StaticBrakeFunctionExport {
  ;
  public static void to(File file) throws IOException {
    Clip clip = LinmotPutHelper.scalePositive();
    Tensor domain = Subdivide.increasing(clip, 500);
    Tensor values = domain.map(StaticBrakeFunction.INSTANCE::getDeceleration);
    Export.of(file, Transpose.of(Tensors.of( //
        domain.map(Magnitude.METER), //
        values.map(Magnitude.ACCELERATION))));
  }

  public static void main(String[] args) throws IOException {
    to(HomeDirectory.file(StaticBrakeFunctionExport.class.getSimpleName() + ".csv"));
  }
}
