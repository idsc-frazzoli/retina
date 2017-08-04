// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.dev.davis240c.DavisImageListener;
import ch.ethz.idsc.retina.dev.davis240c.DavisImageProvider;
import ch.ethz.idsc.retina.dvs.io.aedat.AedatFileSupplier;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;

enum AedatSupplierDemo {
  ;
  private static final File DIRECTORY = new File("/media/datahaki/media/ethz/davis240c/rec1/images");

  public static void main(String[] args) throws Exception {
    final File file1 = new File("/tmp", "DAVIS240C-2017-08-03T16-55-01+0200-02460045-0.aedat");
    final File file2 = new File("/tmp", "DAVIS240C-2017-08-03T18-16-55+0200-02460045-0.aedat");
    File file = file1;
    AedatFileSupplier sup = new AedatFileSupplier(file);
    sup.addListener(new ConsoleDavisEventListener());
    DavisImageProvider davisImageProvider = new DavisImageProvider();
    davisImageProvider.addListener(new DavisImageListener() {
      int count = 0;

      @Override
      public void image(int time, Tensor image) {
        ++count;
        File file = new File(DIRECTORY, String.format("%06d.png", count));
        // System.out.println(Dimensions.of(image));
        try {
          Export.of(file, image);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    });
    sup.addListener(davisImageProvider);
    sup.start();
    // ---
    sup.stop();
  }
}
