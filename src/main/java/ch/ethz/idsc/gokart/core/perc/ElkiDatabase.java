// code by vc, jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;

public enum ElkiDatabase {
  ;
  /** @param matrix with dimension n x d
   * @return database backed by the entries of given matrix */
  public static Database from(Tensor matrix) {
    double[][] data = Primitives.toDoubleArray2D(matrix);
    DatabaseConnection databaseConnection = new ArrayAdapterDatabaseConnection(data);
    Database database = new StaticArrayDatabase(databaseConnection, null);
    database.initialize();
    return database;
  }
}
