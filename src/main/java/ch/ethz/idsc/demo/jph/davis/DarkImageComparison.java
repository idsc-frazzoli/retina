// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;

/* package */ enum DarkImageComparison {
  ;
  private static Tensor image(String name) throws Exception {
    return ResourceData.of("/davis/" + DavisSerial.FX2_02460045.name() + "/" + name + ".png");
  }

  public static void main(String[] args) throws Exception {
    Tensor image0 = image("pitchblack0_part");
    Tensor image1 = image("pitchblack1_part");
    System.out.println(Pretty.of(image1.subtract(image0)));
  }
}
