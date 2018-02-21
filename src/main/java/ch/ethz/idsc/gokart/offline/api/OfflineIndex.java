// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public enum OfflineIndex {
  ;
  public static List<File> folders(File file) throws FileNotFoundException, IOException {
    File dir = file.getParentFile();
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      return bufferedReader.lines() //
          .map(String::trim) //
          .filter(string -> !string.isEmpty()) //
          .filter(string -> !string.startsWith("#")) //
          .map(string -> new File(dir, string)) //
          .filter(File::isDirectory) //
          .collect(Collectors.toList());
    }
  }
}
