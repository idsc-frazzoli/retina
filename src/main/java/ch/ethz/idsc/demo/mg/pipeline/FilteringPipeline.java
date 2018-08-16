// code by jph
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public interface FilteringPipeline {
  boolean filterPipeline(DavisDvsEvent davisDvsEvent);

  double getFilteredPercentage();
}
