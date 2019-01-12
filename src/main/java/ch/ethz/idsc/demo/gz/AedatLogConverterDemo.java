// code by jph
package ch.ethz.idsc.demo.gz;

import java.io.File;

import ch.ethz.idsc.retina.davis.app.AedatLogConverter;

/* package */ enum AedatLogConverterDemo {
  ;
  public static void main(String[] args) throws Exception {
    final File file1 = new File("/tmp", "DAVIS240C-2018-03-19T14-48-29+0100-84010073-0.aedat");
    // final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    // final File file3 = new File("/tmp", "DAVIS240C-2017-08-04T10-13-29+0200-02460045-0.aedat");
    long tic = System.nanoTime();
    AedatLogConverter.of(file1, new File("/tmp/logs"));
    long duration = System.nanoTime() - tic;
    System.out.println((duration * 1e-9) + " [sec]");
  }
}
