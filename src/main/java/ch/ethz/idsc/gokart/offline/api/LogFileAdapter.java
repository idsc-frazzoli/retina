// code by jph
package ch.ethz.idsc.gokart.offline.api;

import java.io.File;

public enum LogFileAdapter {
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
