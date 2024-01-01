package com.example.HonBam.freeboardapi.repository;

import com.example.HonBam.freeboardapi.entity.FreeboardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeboardCommentRepository extends JpaRepository<FreeboardComment, Long> {
}
