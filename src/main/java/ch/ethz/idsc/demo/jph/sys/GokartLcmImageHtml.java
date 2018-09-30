// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

enum GokartLcmImageHtml {
  ;
  public static void main(String[] args) throws FileNotFoundException {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><body>\n");
    for (File file : GokartLcmImageGenerator.DIRECTORY.listFiles()) {
      String string = file.getName();
      string = string.substring(0, string.length() - 4);
      sb.append(String.format("<h3>%s</h3><img src='%s/%s'><br/><br/>\n", //
          string, //
          GokartLcmImageGenerator.DIRECTORY.getName(), //
          file.getName()));
    }
    // System.out.println(sb);
    File index = new File(GokartLcmImageGenerator.DIRECTORY.getParentFile(), "index.html");
    try (PrintWriter printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream(index)))) {
      printWriter.print(sb.toString());
    }
  }
}
