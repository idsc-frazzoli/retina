// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dvs.io.aedat.AedatLogConverter;

enum AedatLogConverterDemo {
  ;
  public static void main(String[] args) throws Exception {
    final File file1 = new File("/tmp", "DAVIS240C-2017-08-03T16-55-01+0200-02460045-0.aedat");
    final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    final File file3 = new File("/tmp", "DAVIS240C-2017-08-04T10-13-29+0200-02460045-0.aedat");
    long tic = System.nanoTime();
    AedatLogConverter.of(file3, new File("/media/datahaki/media/ethz/davis240c/rec4"));
    long duration = System.nanoTime() - tic;
    System.out.println((duration * 1e-9) + " [sec]");
  }
}
