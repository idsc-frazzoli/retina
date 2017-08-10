// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.davis.io.dat.DatFileSupplier;
import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.dvs.digest.DvsEventStatistics;

/** demo dat file read */
enum DatSupplierTest {
  ;
  public static void main(String[] args) throws Exception {
    DvsEventStatistics stats = new DvsEventStatistics();
    int maxx = 0;
    int maxy = 0;
    File file = new File("/media/datahaki/media/ethz/dvs/wp.doc.ic.ac.uk_pb2114_datasets", //
        "jumping.dat");
    try (DatFileSupplier sup = new DatFileSupplier(file, ImageDimensions.IMPERIAL_COLLEGE)) {
      // DvsEventBuffer buf = new DvsEventBuffer(10000);
      // DatFileDigest dfd = new DatFileDigest(UserHome.file("test.dat"));
      while (true) {
        DvsEvent dvsEvent = sup.next();
        maxx = Math.max(dvsEvent.x, maxx);
        maxy = Math.max(dvsEvent.y, maxy);
        stats.digest(dvsEvent);
        // dfd.digest(dvsEvent);
        // buf.digest(dvsEvent);
        // if (count%100000==0)
        // System.out.println(dvsEvent.toString());
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    System.out.println(maxx + " " + maxy);
    stats.printSummary();
  }
}
