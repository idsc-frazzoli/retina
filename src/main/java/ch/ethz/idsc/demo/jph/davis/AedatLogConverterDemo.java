// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.retina.davis.app.AedatLogConverter;
import ch.ethz.idsc.tensor.io.Timing;

/* package */ enum AedatLogConverterDemo {
  ;
  public static void main(String[] args) throws Exception {
    Timing timing = Timing.started();
    AedatLogConverter.of(Aedat20.LOG_01.file, new File("/media/datahaki/media/ethz/davis240c/rec4a"));
    System.out.println(timing.seconds() + "[s]");
  }
}
