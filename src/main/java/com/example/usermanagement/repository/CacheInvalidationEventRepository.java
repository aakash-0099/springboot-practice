package com.example.usermanagement.repository;

import com.example.usermanagement.entity.CacheInvalidationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CacheInvalidationEventRepository extends JpaRepository<CacheInvalidationEvent, Long> {

    List<CacheInvalidationEvent> findAllByOrderByIdAsc();
}
