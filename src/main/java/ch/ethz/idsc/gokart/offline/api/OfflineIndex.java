// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.idsc.tensor.io.ReadLine;

public enum OfflineIndex {
  ;
  public static List<File> folders(File file) throws FileNotFoundException, IOException {
    File dir = file.getParentFile();
    try (InputStream inputStream = new FileInputStream(file)) {
      return ReadLine.of(inputStream) //
          .map(String::trim) //
          .filter(string -> !string.isEmpty()) //
          .filter(string -> !string.startsWith("#")) //
          .map(string -> new File(dir, string)) //
          .filter(File::isDirectory) //
          .collect(Collectors.toList());
    }
  }
}
