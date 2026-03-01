package com.app.src.daos;

import java.util.List;

// 1. The Generic Interface
public interface BaseDAO<T> {
    T findById(String id);
    List<T> findAll();
    boolean create(T entity);
    boolean update(T entity);
    boolean delete(String id);
}

