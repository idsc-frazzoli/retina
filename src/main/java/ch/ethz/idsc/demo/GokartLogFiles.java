// code by jph
package ch.ethz.idsc.demo;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.gokart.offline.api.LogFile;

public enum GokartLogFiles {
  ;
  public static Collection<LogFile> all(File root) {
    return Stream.of(root.listFiles()) //
        .filter(File::isDirectory) //
        .flatMap(directory -> Stream.of(directory.listFiles())) //
        .filter(File::isFile) //
        .filter(StaticHelper::hasLcmExtension) //
        .sorted() //
        .map(LogFileAdapter::from) //
        .collect(Collectors.toList());
  }
}
