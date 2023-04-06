package com.sas.sso.dao;

import com.sas.sso.entity.CompanyMaster;
import com.sas.sso.entity.User;
import com.sas.sso.request.UserRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    EntityManager entityManager;
    @Override
    public List<User> getUserDetailsByFilter(UserRequest request) {
        try {
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
            Root<User> userRoot = criteria.from(User.class);
            Join<User, CompanyMaster> companyJoin = userRoot.join("companyMaster");
            List<Predicate> predicates = getPredicates(request, criteriaBuilder, userRoot,companyJoin);
            criteria.select(userRoot).where(predicates.toArray(new Predicate[predicates.size()]));
            return entityManager.createQuery(criteria).setFirstResult(request.getStartIndex()).setMaxResults(request.getEndIndex()).getResultList();
        } catch (Exception e) {
            e.getMessage();

        }
        return null;
    }

    private List<Predicate> getPredicates(UserRequest request, CriteriaBuilder criteriaBuilder, Root<User> userRoot, Join<User, CompanyMaster> companyJoin) {
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.hasText(request.getFirstName())){
            predicates.add(criteriaBuilder.equal(userRoot.get("firstName"), request.getFirstName()));
        }
        if(StringUtils.hasText(request.getEmail())){
            predicates.add(criteriaBuilder.equal(userRoot.get("email"), request.getEmail()));
        }
        if(request.getCompId() != null ){
            predicates.add(criteriaBuilder.equal(companyJoin.get("companyId"),request.getCompId()));
        }
        return predicates;
    }
}
