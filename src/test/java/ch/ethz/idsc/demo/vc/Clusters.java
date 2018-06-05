// code by vc
package ch.ethz.idsc.demo.vc;

import java.util.List;

import ch.ethz.idsc.gokart.core.perc.ElkiDatabase;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.data.Cluster;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.data.type.TypeUtil;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDIter;
import de.lmu.ifi.dbs.elki.database.ids.DBIDRange;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.distance.distancefunction.minkowski.SquaredEuclideanDistanceFunction;

/** initial draft for testing of elki library and DBScan algorithm */
/* package */ enum Clusters {
  ;
  /** @param matrix
   * @return tensor of clusters */
  public static Tensor elkiDBSCAN(Tensor matrix, double eps, int minPoints) {
    Database database = ElkiDatabase.from(matrix);
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
        Tensor pr = Tensors.empty();
        DBIDs ids = cluster.getIDs();
        Relation<NumberVector> rel = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange id = (DBIDRange) rel.getDBIDs();
        for (DBIDIter iter = ids.iter(); iter.valid(); iter.advance()) {
          int offset = id.getOffset(iter);
          pr.append(matrix.get(offset));
        }
        pi.append(pr);
      }
    return pi;
  }
}
