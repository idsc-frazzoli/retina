// code by jph
package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.util.Objects;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.tensor.io.UserName;
import junit.framework.TestCase;

public class GokartLogFileTest extends TestCase {
  public void test_datahaki() {
    if (UserName.is("datahaki"))
      for (GokartLogFile gokartLogFile : GokartLogFile.values())
        try {
          File file = DatahakiLogFileLocator.file(gokartLogFile);
          if (Objects.nonNull(file)) {
            File host = file.getParentFile();
            File marker = new File(host.getParentFile(), host.getName() + "_");
            if (!marker.isDirectory()) {
              System.err.println("log file missing: " + gokartLogFile);
              // fail();
            }
          }
        } catch (Exception exception) {
          // exception.printStackTrace();
          String string = exception.getMessage();
          if (Objects.nonNull(string))
            System.err.println(exception.getMessage());
        }
  }

  public void testTitle() {
    assertEquals(/**/ "20180522T135700", //
        GokartLogFile._20180522T135700_2da7e1f5.getTitle());
  }
}
