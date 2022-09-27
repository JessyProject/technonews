package com.jessy.technonews.repository;

import com.jessy.technonews.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> findDisabledUsers() {
        TypedQuery<User> query = entityManager.createNamedQuery("User.findDisabled", User.class);
        return query.getResultList();
    }
}
