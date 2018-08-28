// code by jph
package ch.ethz.idsc.demo;

import java.io.File;

import ch.ethz.idsc.gokart.offline.api.LogFile;

/* package */ enum LogFileAdapter {
  ;
  public static LogFile from(File file) {
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
}
