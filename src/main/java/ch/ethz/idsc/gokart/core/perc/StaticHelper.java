// code by vc, jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;

enum StaticHelper {
  ;
  /** @param matrix with dimension n x 2
   * @return database backed by the entries of given matrix */
  static Database database(Tensor matrix) {
    double[][] data = Primitives.toDoubleArray2D(matrix);
    DatabaseConnection databaseConnection = new ArrayAdapterDatabaseConnection(data);
    Database database = new StaticArrayDatabase(databaseConnection, null);
    database.initialize();
    return database;
  }
}
