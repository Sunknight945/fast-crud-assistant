package com.uiys.jpa.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface BaseJpaRepository<T, Id> extends JpaRepository<T, Id>, JpaSpecificationExecutor<T>,
  QuerydslPredicateExecutor<T> {
}
