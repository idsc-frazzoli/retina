// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class AedatFileHeader {
  private static final String HEADER_TERMINATOR_20 = "#End Of ASCII Header";
  private static final String HEADER_TERMINATOR_31 = "#!END-HEADER";
  /** lines of header in log file */
  private final List<String> header = new LinkedList<>();
  private final InputStream inputStream;

  public AedatFileHeader(File file) throws IOException {
    int skip = 0;
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      while (true) {
        String string = bufferedReader.readLine();
        header.add(string);
        skip += string.length() + 2; // add 2 characters of line break
        if (string.equals(HEADER_TERMINATOR_20) || string.equals(HEADER_TERMINATOR_31))
          break;
      }
    }
    inputStream = new FileInputStream(file);
    inputStream.skip(skip);
  }

  public InputStream getInputStream() {
    return inputStream;
  }
}
