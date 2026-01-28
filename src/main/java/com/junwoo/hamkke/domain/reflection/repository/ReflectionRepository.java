package com.junwoo.hamkke.domain.reflection.repository;

import com.junwoo.hamkke.domain.reflection.entity.ReflectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author junnukim1007gmail.com
 * @date 26. 1. 28.
 */
public interface ReflectionRepository extends JpaRepository<ReflectionEntity, Long> {
}
