package com.example.demo1.Repository;

import com.example.demo1.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImageRepository extends MongoRepository<Image, String> {
    List<Image> findByUsername(String username);
}
