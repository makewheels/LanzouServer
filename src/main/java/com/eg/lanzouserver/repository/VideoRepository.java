package com.eg.lanzouserver.repository;

import com.eg.lanzouserver.bean.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @time 2020-02-02 00:03
 */
public interface VideoRepository extends MongoRepository<Video, String> {

}
