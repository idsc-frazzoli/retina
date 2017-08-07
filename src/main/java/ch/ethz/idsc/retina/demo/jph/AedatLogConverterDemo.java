// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dvs.app.AedatLogConverter;

enum AedatLogConverterDemo {
  ;
  public static void main(String[] args) throws Exception {
    long tic = System.nanoTime();
    AedatLogConverter.of(Aedat.LOG_04.file, new File("/media/datahaki/media/ethz/davis240c/rec4a"));
    long duration = System.nanoTime() - tic;
    System.out.println((duration * 1e-9) + " [sec]");
  }
}
