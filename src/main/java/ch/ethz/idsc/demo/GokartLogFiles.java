// code by jph
package ch.ethz.idsc.demo;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.offline.api.LogFile;

public enum GokartLogFiles {
  ;
  private static final String SUFFIX = ".lcm.00";

  static boolean hasLcmExtension(File file) {
    return file.getName().endsWith(SUFFIX);
  }

  static LogFile from(File file) {
    return new LogFile() {
      @Override
      public String getFilename() {
        return file.getName();
      }

      @Override
      public String getTitle() {
        return file.getName().substring(0, 15);
      }
    };
  }

  public static Collection<LogFile> all(File root) {
    return Stream.of(root.listFiles()) //
        .filter(File::isDirectory) //
        .flatMap(directory -> Stream.of(directory.listFiles())) //
        .filter(File::isFile) //
        .filter(GokartLogFiles::hasLcmExtension) //
        .sorted() //
        .map(GokartLogFiles::from) //
        .collect(Collectors.toList());
  }
}
