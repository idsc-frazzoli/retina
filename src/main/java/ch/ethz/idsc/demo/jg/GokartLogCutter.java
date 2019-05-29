//code by jph
package ch.ethz.idsc.demo.jg;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmLogCutter;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum GokartLogCutter {
  ;
  public static void main(String[] args) throws IOException {
    Optional<File> optional = FileHelper.open(args);
    if (optional.isPresent()) {
      File file = optional.get(); // original log file
      File dest = new File(file.getParentFile(), "cuts"); // destination folder
      String name = file.getName().split("T")[0]; // subdirectory title
      dest.mkdir();
      GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
      new GokartLcmLogCutter(gokartLogFileIndexer, dest, name);
    }
  }
}