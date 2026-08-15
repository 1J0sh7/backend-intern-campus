# Performance Diagnosis — N+1 Queries

## Problem
When fetching customers with their addresses, Hibernate runs 1 query for customers + N queries for addresses.

## Fix
Used `@EntityGraph` and `JOIN FETCH` to fetch related data in a single query.

## Evidence
- Before: 101 queries for 100 customers
- After: 1 query for 100 customers with addresses