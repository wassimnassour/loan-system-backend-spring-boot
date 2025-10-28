package com.example.demo.specification.Loan;

import com.example.demo.dto.request.loan.LoanFilter;
import com.example.demo.enums.EnumRole;
import com.example.demo.model.Loan;
import com.example.demo.model.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class LoanSpecification {
   public static Specification<Loan> withFilters(LoanFilter filter , User currentUser) {
       return (root , query , criteriaBuilder)-> {
           List<Predicate> predicetsList = new ArrayList<Predicate>();

           if(currentUser.getRoles().contains(EnumRole.USER)){
               predicetsList.add(criteriaBuilder.equal(root.get("user_id") , currentUser.getId() ));
           };

           if(filter.getStartDate()!=null) {
               predicetsList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getStartDate()));
           }

           if(filter.getEndDate()!=null) {
           predicetsList.add(criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"),filter.getEndDate()));
           }

           if(filter.getStatus()!=null) {
               predicetsList.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
           }
           return criteriaBuilder.and(predicetsList.toArray(new Predicate[0]));
       };
   }
}