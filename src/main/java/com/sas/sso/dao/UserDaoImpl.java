package com.sas.sso.dao;

import com.sas.sso.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    EntityManager entityManager;
    @Override
    public User findUserByIdOrNameOrEmail(Long id, String name, String email) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = criteria.from(User.class);
            List<Predicate> predicates = getPredicates(id, name, email, criteriaBuilder, userRoot);
            criteria.select(userRoot).where(predicates.toArray(new Predicate[predicates.size()]));
            return  entityManager.createQuery(criteria).getSingleResult();
        } catch (Exception e) {
            e.getMessage();

        }
        return null;
    }

    private List<Predicate> getPredicates( Long id,String name, String email, CriteriaBuilder criteriaBuilder, Root<User> userRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(id!=null){
            predicates.add(criteriaBuilder.equal(userRoot.get("id"), id));
        }
        if(name != null){
            predicates.add(criteriaBuilder.equal(userRoot.get("name"), name));
        }
        if(email!=null){
            predicates.add(criteriaBuilder.equal(userRoot.get(email),email));
        }
        return predicates;
    }
}
