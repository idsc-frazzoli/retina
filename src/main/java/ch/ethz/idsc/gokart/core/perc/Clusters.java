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

public enum Clusters {
  ;
  /** @param p
   * @return unstructured */
  // TODO remove print outs. provide timing and properties in separate class if necessary
  // TODO also handle empty input
  public static Tensor elkiDBSCAN(Tensor p) {
    Database db = Clusters.sample(p);
    Stopwatch stopwatch = Stopwatch.started();
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, ClusterConfig.GLOBAL.getEpsilon(), ClusterConfig.GLOBAL.getMinPoints());
    Clustering<Model> result = dbscan.run(db);
    long ns = stopwatch.display_nanoSeconds();
    System.out.println((ns * 1e-6) + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    // System.out.println("Number of clusters: " + allClusters.size());
    // ---
    Tensor pi = Tensors.empty();
    for (Cluster<Model> cluster : allClusters) {
      Tensor pr = Tensors.empty();
      // System.out.println("Cluster size:" + cluster.size());
      // System.out.println("Is noise:" + cluster.isNoise());
      if (!cluster.isNoise()) {
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = db.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          // System.out.println("offset:" + offset);
          // System.out.println(p.get(offset));
          pr.append(Tensors.of(p.get(offset)));
        }
        pi.append(pr);
      }
    }
    // System.out.println("end");
    return pi;
  }

  static Database sample(Tensor p) {
    double[][] data = fromMatrix(p);
    DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
    Database db = new StaticArrayDatabase(dbc, null);
    db.initialize();
    return db;
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
