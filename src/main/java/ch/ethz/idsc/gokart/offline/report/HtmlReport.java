// code by jph
package ch.ethz.idsc.gokart.offline.report;

import java.io.File;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;

public enum HtmlReport {
  ;
  public static void of(LogFileSummary logFileSummary, File fileHtml) {
    Optional<Scalar> optional = logFileSummary.getLocalizationStart();
    if (optional.isPresent())
      System.out.println("start time = " + optional.get());
  }
}
