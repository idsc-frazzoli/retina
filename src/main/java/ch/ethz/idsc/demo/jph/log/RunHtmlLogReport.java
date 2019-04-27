// code by jph
package ch.ethz.idsc.demo.jph.log;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.gokart.offline.gui.GokartLcmMap;
import ch.ethz.idsc.gokart.offline.gui.HtmlLogReport;

/* package */ enum RunHtmlLogReport {
  ;
  public static void main(String[] args) throws IOException {
    // HtmlLogReport.generate(new File(StaticHelper.DEST, "20190208/20190208T145312_04"));
    File file = new File(StaticHelper.DEST, "20190211/20190211T100755_00");
    new HtmlLogReport(new GokartLcmMap(file), file.getName(), file);
  }
}
