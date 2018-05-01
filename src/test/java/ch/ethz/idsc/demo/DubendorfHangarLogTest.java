// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import junit.framework.TestCase;

public class DubendorfHangarLogTest extends TestCase {
  public void test_datahaki() {
    String username = UserHome.file("").getName();
    if (username.equals("datahaki")) {
      File directory = new File("/media/datahaki/media/ethz/gokartlogs");
      for (DubendorfHangarLog dhl : DubendorfHangarLog.values()) {
        File file = dhl.file(directory);
        boolean isFile = file.isFile();
        if (!isFile) {
          File host = file.getParentFile();
          File marker = new File(host.getParentFile(), host.getName() + "_");
          if (!marker.isDirectory()) {
            System.err.println("log file missing: " + dhl);
            assertTrue(false);
          }
        }
      }
    }
  }
}
