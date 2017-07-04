// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.core.DvsEvent;
import ch.ethz.idsc.retina.digest.DvsEventBuffer;
import ch.ethz.idsc.retina.digest.DvsEventStatistics;
import ch.ethz.idsc.retina.io.dat.DatFileSupplier;
import ch.ethz.idsc.retina.supply.DvsEventSupplier;
import ch.ethz.idsc.retina.util.io.ImageDimensions;

/** demo dat file read */
class DatSupplierTest {
  public static void main(String[] args) throws Exception {
    DvsEventStatistics stats = new DvsEventStatistics();
    int maxx = 0;
    int maxy = 0;
    try {
      File file = new File("/media/datahaki/media/ethz/dvs/wp.doc.ic.ac.uk_pb2114_datasets", //
          "jumping.dat");
      DvsEventSupplier sup = new DatFileSupplier(file, ImageDimensions.IMPERIAL_COLLEGE);
      DvsEventBuffer buf = new DvsEventBuffer(10000);
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
