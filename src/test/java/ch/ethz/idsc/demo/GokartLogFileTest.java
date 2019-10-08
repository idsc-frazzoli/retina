// code by jph
package ch.ethz.idsc.demo;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  public void testSorted() {
    GokartLogFile prev = null;
    for (GokartLogFile next : GokartLogFile.values()) {
      if (Objects.nonNull(prev)) {
        int compareTo = prev.name().compareTo(next.name());
        if (compareTo > 0)
          System.err.println("not sorted: " + next);
      }
      prev = next;
    }
    List<String> l1 = Stream.of(GokartLogFile.values()).map(GokartLogFile::name).collect(Collectors.toList());
    List<String> l2 = Stream.of(GokartLogFile.values()).map(GokartLogFile::name).sorted().collect(Collectors.toList());
    assertEquals(l1, l2);
  }
}
