package com.eg.lanzouserver.repository;

import com.eg.lanzouserver.bean.lanzou.LanzouFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @time 2020-02-03 15:46
 */
@Repository
public interface LanzouFileRepository extends MongoRepository<LanzouFile, String> {

}
