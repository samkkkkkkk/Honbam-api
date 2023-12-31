package com.example.HonBam.postapi.repository;

import com.example.HonBam.postapi.entity.SnsLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<SnsLike, Long> {

}
