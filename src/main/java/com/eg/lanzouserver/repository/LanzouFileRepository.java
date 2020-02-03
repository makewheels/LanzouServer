package com.eg.lanzouserver.repository;

import com.eg.lanzouserver.bean.lanzou.LanzouFile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @time 2020-02-03 15:46
 */
public interface LanzouFileRepository extends MongoRepository<LanzouFile, String> {

}
