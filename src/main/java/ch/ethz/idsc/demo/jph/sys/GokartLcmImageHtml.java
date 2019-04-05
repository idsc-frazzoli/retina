// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.io.File;
import java.util.stream.Stream;

import ch.ethz.idsc.subare.util.HtmlUtf8;

/* package */ class GokartLcmImageHtml implements AutoCloseable {
  private final HtmlUtf8 htmlUtf8;

  public GokartLcmImageHtml(File file) {
    htmlUtf8 = HtmlUtf8.page(file);
    htmlUtf8.appendln("<html><body>");
  }

  public void process(File file) {
    String string = file.getName();
    string = string.substring(0, string.length() - 4);
    htmlUtf8.appendln(String.format("<h3>%s</h3><img src='%s/%s'><br/><br/>", //
        string, //
        GokartLcmImageGenerator.DIRECTORY.getName(), //
        file.getName()));
  }

  @Override // from AutoCloseable
  public void close() throws Exception {
    htmlUtf8.close();
  }

  public static void main(String[] args) throws Exception {
    File index = new File(GokartLcmImageGenerator.DIRECTORY.getParentFile(), "index.html");
    try (GokartLcmImageHtml gokartLcmImageHtml = new GokartLcmImageHtml(index)) {
      Stream.of(GokartLcmImageGenerator.DIRECTORY.listFiles()).sorted().forEach(gokartLcmImageHtml::process);
    }
  }
}
