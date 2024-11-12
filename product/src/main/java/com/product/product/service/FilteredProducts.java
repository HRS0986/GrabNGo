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

        // Add filters based on the provided parameters
        if (categoryId > 0) {
            predicates.add(cb.equal(product.get("categoryId"), categoryId));
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

        // Set where clause with all predicates
        query.where(predicates.toArray(new Predicate[0]));

        // Log the pagination parameters
//        System.out.println("Page offset: " + pageable.getOffset());
//        System.out.println("Page size: " + pageable.getPageSize());
//        System.out.println("Sort order: " + pageable.getSort());

        // Apply sorting based on Pageable's Sort information
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    query.orderBy(cb.asc(product.get(order.getProperty())));
                } else {
                    query.orderBy(cb.desc(product.get(order.getProperty())));
                }
            });
        }

        // Create the query and apply pagination
        TypedQuery<Product> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset()); // Set the starting position
        typedQuery.setMaxResults(pageable.getPageSize()); // Set the maximum number of results per page

        // Fetch the filtered list of products
        List<Product> products;
        try {
            products = typedQuery.getResultList();
//            System.out.println("Fetched " + products.size() + " products for the current page.");
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("Error executing query: " + e.getMessage());
            return Page.empty(); // Return an empty page if an error occurs
        }

        // Get the total count of records matching the filter conditions
        long totalResults = getTotalCount(cb, predicates);
//        System.out.println("Total results matching the filter criteria: " + totalResults);

        // Return a Page object with the results, Pageable, and total count
        return new PageImpl<>(products, pageable, totalResults);
    }

    private long getTotalCount(CriteriaBuilder cb, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);

        // Select the count
        countQuery.select(cb.count(countRoot));

        // Apply predicates
        Predicate[] predicateArray = new Predicate[predicates.size()];
        predicates.toArray(predicateArray);
        countQuery.where(predicateArray);

        // Debugging step
        System.out.println("Total Count Query Predicates: " + predicates.size());

        // Execute and return the count
        long count;
        try {
            count = entityManager.createQuery(countQuery).getSingleResult();
            System.out.println("Total count of products matching criteria: " + count);
        } catch (Exception e) {
//            e.printStackTrace();
            System.err.println("Error fetching total count: " + e.getMessage());
            count = 0;
        }
        return count;
    }
}
