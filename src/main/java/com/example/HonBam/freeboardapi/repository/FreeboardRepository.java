package com.example.HonBam.freeboardapi.repository;

import com.example.HonBam.freeboardapi.entity.Freeboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FreeboardRepository extends
        JpaRepository<Freeboard, Long> {


}
