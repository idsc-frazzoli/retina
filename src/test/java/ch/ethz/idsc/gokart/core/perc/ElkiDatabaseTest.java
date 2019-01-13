// code by jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import de.lmu.ifi.dbs.elki.database.Database;
import junit.framework.TestCase;

public class ElkiDatabaseTest extends TestCase {
  public void testSimple() {
    Database database = ElkiDatabase.from(HilbertMatrix.of(10, 2));
    assertNotNull(database);
  }
}
