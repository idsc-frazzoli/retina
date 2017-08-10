// code by jph
package ch.ethz.idsc.retina.demo.shili;

import java.io.File;

import ch.ethz.idsc.retina.davis.io.aedat.AedatLogConverter;

enum AedatLogConverterDemo {
  ;
  @SuppressWarnings("unused")
  public static void main(String[] args) throws Exception {
    // TODO configure
    final File file1 = new File("/home/ale/Datasets", "DAVIS240C-2017-08-04T11-56-59+0200-02460038-0.aedat"); // input location
    final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    final File file3 = new File("/tmp", "DAVIS240C-2017-08-04T10-13-29+0200-02460045-0.aedat");
    long tic = System.nanoTime();
    AedatLogConverter.of(file1, new File("/home/ale/Datasets/rec1")); // output location
    long duration = System.nanoTime() - tic;
    System.out.println((duration * 1e-9) + " [sec]");
  }
}
