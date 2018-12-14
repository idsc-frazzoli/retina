// code by vc
package ch.ethz.idsc.gokart.core.perc;

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
/* package */ enum Dbscan {
  ;
  /** @param matrix of points
   * @param eps
   * @param minPoints
   * @return tensor of clusters of points */
  public static Tensor of(Tensor matrix, double eps, int minPoints) {
    Database database = ElkiDatabase.from(matrix);
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, eps, minPoints);
    Clustering<Model> clustering = dbscan.run(database);
    Tensor collection = Tensors.empty();
    for (Cluster<Model> cluster : clustering.getAllClusters())
      if (!cluster.isNoise()) {
        DBIDs dbids = cluster.getIDs();
        Relation<NumberVector> relation = database.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        DBIDRange dbidRange = (DBIDRange) relation.getDBIDs();
        Tensor entry = Tensors.empty();
        for (DBIDIter iter = dbids.iter(); iter.valid(); iter.advance()) {
          int offset = dbidRange.getOffset(iter);
          entry.append(matrix.get(offset));
        }
        collection.append(entry);
      }
    return collection;
  }
}
