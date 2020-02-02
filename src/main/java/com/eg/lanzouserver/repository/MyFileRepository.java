package com.eg.lanzouserver.repository;

import com.eg.lanzouserver.bean.MyFile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @time 2020-02-02 00:03
 */
public interface MyFileRepository extends MongoRepository<MyFile, String> {
    MyFile findMyFileByTsIdEquals(String tsId);

}
