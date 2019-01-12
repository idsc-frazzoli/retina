// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.retina.davis.app.AedatLogConverter;

enum AedatLogConverterDemo {
  ;
  public static void main(String[] args) throws Exception {
    long tic = System.nanoTime();
    AedatLogConverter.of(Aedat20.LOG_01.file, new File("/media/datahaki/media/ethz/davis240c/rec4a"));
    long duration = System.nanoTime() - tic;
    System.out.println((duration * 1e-9) + " [sec]");
  }
}
