package com.eg.lanzouserver.repository;

import com.eg.lanzouserver.bean.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @time 2020-02-02 00:03
 */
@Repository
public interface VideoRepository extends MongoRepository<Video, String> {

}
