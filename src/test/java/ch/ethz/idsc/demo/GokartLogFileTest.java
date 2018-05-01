// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.owl.bot.util.UserHome;
import junit.framework.TestCase;

public class GokartLogFileTest extends TestCase {
  public void test_datahaki() {
    String username = UserHome.file("").getName();
    if (username.equals("datahaki"))
      for (GokartLogFile gokartLogFile : GokartLogFile.values())
        try {
          File file = DatahakiLogFileLocator.file(gokartLogFile);
          // dhl.file(directory);
          boolean isFile = file.isFile();
          if (!isFile) {
            File host = file.getParentFile();
            File marker = new File(host.getParentFile(), host.getName() + "_");
            if (!marker.isDirectory()) {
              System.err.println("log file missing: " + gokartLogFile);
              // assertTrue(false);
            }
          }
        } catch (Exception exception) {
          // exception.printStackTrace();
          System.out.println(exception.getMessage());
        }
  }
}
