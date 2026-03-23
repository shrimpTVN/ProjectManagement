package com.server.dao;

import java.util.List;

// 1. The Generic Interface
public interface BaseDAO<T> {
    T findById(int id) ;
    List<T> findAll();
    boolean create(T entity);
    boolean update(int id, T entity);
    boolean delete(int id);
}
