package com.example.HonBam.postapi.repository;

import com.example.HonBam.postapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends
        JpaRepository<Comment, Long> {

}
