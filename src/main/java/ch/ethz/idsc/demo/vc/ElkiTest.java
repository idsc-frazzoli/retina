// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.gokart.core.perc.Clusters;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;

public enum ElkiTest {
  ;
  // static Tensor pi = Tensors.empty();
  static double[][] fromMatrix(Tensor matrix) {
    final int cols = Unprotect.dimension1(matrix);
    double[][] array = new double[matrix.length()][cols];
    for (int row = 0; row < matrix.length(); ++row)
      for (int col = 0; col < cols; ++col)
        array[row][col] = matrix.Get(row, col).number().doubleValue();
    return array;
  }

  public static Database sample(Tensor p) {
    double[][] data = fromMatrix(p);
    DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
    Database db = new StaticArrayDatabase(dbc, null);
    db.initialize();
    return db;
  }

  public static void main(String[] args) {
    double[][] data = new double[10][2];
    data[3][0] = 0.98;
    data[3][1] = 0.1;
    data[0][0] = 0.99;
    data[0][1] = 0.16;
    data[2][0] = 2.16;
    data[2][1] = 3.99;
    data[1][0] = 2.1;
    data[1][1] = 3.98;
    Tensor p = Tensors.matrixDouble(data);
    Tensor testDBSCANResults = Clusters.elkiDBSCAN(p);
  }
}
