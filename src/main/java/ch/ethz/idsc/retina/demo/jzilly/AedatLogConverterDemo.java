// code by jph
package ch.ethz.idsc.retina.demo.jzilly;

import java.io.File;

import ch.ethz.idsc.retina.dev.davis.app.AedatLogConverter;

enum AedatLogConverterDemo {
  ;
  public static void main(String[] args) throws Exception {
    final File file1 = new File("/Users/julianzilly/Desktop/Projects/logged_data/", "DAVIS240C-2017-08-25T15-42-35+0200-02460010-0.aedat");
    long tic = System.nanoTime();
    AedatLogConverter.of(file1, new File("/Users/julianzilly/Desktop/Projects/logged_data/rec1/"));
    long duration = System.nanoTime() - tic;
    System.out.println((duration * 1e-9) + " [sec]");
  }
}
