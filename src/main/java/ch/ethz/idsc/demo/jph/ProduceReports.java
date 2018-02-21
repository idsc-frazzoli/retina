package ch.ethz.idsc.demo.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.demo.DubendorfHangarLog;
import ch.ethz.idsc.gokart.offline.report.HtmlReport;
import ch.ethz.idsc.gokart.offline.report.LogFileSummary;

enum ProduceReports {
  ;
  private static final File LOG_ROOT = new File("/media/datahaki/media/ethz/gokartlogs");

  public static void main(String[] args) throws IOException {
    for (DubendorfHangarLog dhl : DubendorfHangarLog.values())
      if (dhl.name().startsWith("_20171218")) {
        System.out.println(dhl);
        File file = dhl.file(LOG_ROOT);
        LogFileSummary logFileSummary = LogFileSummary.of(file);
        HtmlReport.of(logFileSummary, null);
      }
  }
}
