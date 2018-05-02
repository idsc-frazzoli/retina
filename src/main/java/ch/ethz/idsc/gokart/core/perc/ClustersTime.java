// code by vc
package ch.ethz.idsc.gokart.core.perc;

import java.util.List;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;

public enum ClustersTime {
  ;
  /** @param matrix
   * @return tensor of clusters */
  // TODO remove print outs. provide timing and properties in separate class if necessary
  // TODO also handle empty input
  public static Tensor elkiDBSCAN(Tensor matrix, double eps, int minPoints) {
    Database database = ClustersTime.sample(matrix);
    int length = matrix.length() / 2;
    Stopwatch stopwatch = Stopwatch.started();
    DBSCAN<NumberVector> dbscan = //
        new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, eps, minPoints);
    Clustering<Model> result = dbscan.run(database);
    long ns = stopwatch.display_nanoSeconds();
    System.out.println((ns * 1e-6) + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    Tensor pi = Tensors.empty();
    for (Cluster<Model> cluster : allClusters)
      if (!cluster.isNoise()) {
        Tensor p1 = Tensors.empty();
        Tensor p2 = Tensors.empty();
        Tensor p3 = Tensors.empty();
        Tensor p4 = Tensors.empty();
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          if (offset < length) {
            if (offset < length / 2)
              p1.append(matrix.get(offset));
            else
              p2.append(matrix.get(offset));
          } else {
            if (offset >= 3 * length / 2)
              p4.append(matrix.get(offset));
            else
              p3.append(matrix.get(offset));
          }
        }
        // System.out.println("p2"+p2);
        Tensor of = Tensors.of(p1, p2, p3, p4);
        // System.out.println("of"+ of);
        // System.out.println("p1"+p1);
        pi.append(of);
      }
    return pi;
  }

  static Database sample(Tensor matrix) {
    double[][] data = fromMatrix(matrix);
    DatabaseConnection databaseConnection = new ArrayAdapterDatabaseConnection(data);
    Database database = new StaticArrayDatabase(databaseConnection, null);
    database.initialize();
    return database;
  }

  // TODO TENSOR V052
  static double[][] fromMatrix(Tensor matrix) {
    final int cols = Unprotect.dimension1(matrix);
    double[][] array = new double[matrix.length()][cols];
    for (int row = 0; row < matrix.length(); ++row)
      for (int col = 0; col < cols; ++col)
        array[row][col] = matrix.Get(row, col).number().doubleValue();
    return array;
  }
}
