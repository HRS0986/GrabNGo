package com.product.product.service;

import com.product.product.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class FilteredProducts {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Product> getFilteredProducts(int categoryId, Double minPrice, Double maxPrice, String search, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        // Corrected field name to match `Product` entity
        if (categoryId > 0) {
            predicates.add(cb.equal(product.get("productCategoryId"), categoryId));
        }

        if (minPrice != null) {
            predicates.add(cb.greaterThanOrEqualTo(product.get("productPrice"), minPrice));
        }

        if (maxPrice != null) {
            predicates.add(cb.lessThanOrEqualTo(product.get("productPrice"), maxPrice));
        }

        if (StringUtils.hasText(search)) {
            predicates.add(cb.like(cb.lower(product.get("productName")), "%" + search.toLowerCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Product> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(typedQuery.getResultList(), pageable, getTotalCount(cb, predicates));
    }

    private long getTotalCount(CriteriaBuilder cb, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
