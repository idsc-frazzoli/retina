// code by jph
package ch.ethz.idsc.demo.jph.dvs;

import java.io.File;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.retina.dvs.digest.DvsEventBuffer;
import ch.ethz.idsc.retina.dvs.digest.DvsEventStatistics;
import ch.ethz.idsc.retina.dvs.supply.DvsTxtFileSupplier;

/** demo events.txt file read
 * file format used by Robotics and Perception Group
 * http://rpg.ifi.uzh.ch/ */
enum TxtSupplierTest {
  ;
  public static void main(String[] args) throws Exception {
    DvsEventStatistics stats = new DvsEventStatistics();
    int maxx = 0;
    int maxy = 0;
    File file = new File("/media/datahaki/media/ethz/davis/shapes_6dof", //
        "events.txt");
    System.out.println(file.exists());
    try (DvsTxtFileSupplier sup = new DvsTxtFileSupplier(file, ImageDimensions.UZ)) {
      DvsEventBuffer buf = new DvsEventBuffer(10000);
      while (true) {
        DvsEvent dvsEvent = sup.next();
        maxx = Math.max(dvsEvent.x, maxx);
        maxy = Math.max(dvsEvent.y, maxy);
        stats.digest(dvsEvent);
        buf.digest(dvsEvent);
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
