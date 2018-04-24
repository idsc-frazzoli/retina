// code by jph
package ch.ethz.idsc.gokart.core.perc;

import java.util.List;

import ch.ethz.idsc.demo.vc.ElkiTest;
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

public enum Clusters {
  ;
  public static Tensor testDBSCANResults(Tensor p) {
    Tensor pi = Tensors.empty();
    Database db = ElkiTest.sample(p);
    long nanoTime = System.nanoTime();
    // TODO tuning of parameters
    DBSCAN<NumberVector> dbscan = new DBSCAN<>(SquaredEuclideanDistanceFunction.STATIC, ClusterConfig.GLOBAL.getEpsilon(), ClusterConfig.GLOBAL.getMinPoints());
    Clustering<Model> result = dbscan.run(db);
    long nanoTime2 = System.nanoTime();
    System.out.println((nanoTime2 - nanoTime) * 0.000001 + "ms");
    List<Cluster<Model>> allClusters = result.getAllClusters();
    System.out.println("Number of clusters: " + allClusters.size());
    for (Cluster<Model> cluster : allClusters) {
      Tensor pr = Tensors.empty();
      System.out.println("Cluster size:" + cluster.size());
      System.out.println("Is noise:" + cluster.isNoise());
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
    System.out.println("end");
    return (pi);
  }
}
