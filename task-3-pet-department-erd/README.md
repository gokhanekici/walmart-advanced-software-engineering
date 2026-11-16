# Task 3 – Pet Department ERD

## Problem

Design a normalized relational schema that merges the pet department's databases into a single source of truth. It covers:

- Products (food, toys, apparel), each with a manufacturer and one or more target animals
- Customers and their purchase transactions
- Shipments of products between Walmart locations

## Diagram

📄 [pet-department-erd.pdf](pet-department-erd.pdf) · Source: [pet-department-erd.dbml](pet-department-erd.dbml)

## Tables

| Group | Tables |
|-------|--------|
| Products | `product`, `pet_food`, `pet_toy`, `pet_apparel`, `manufacturer` |
| Animals | `animal`, `product_animal` |
| Sales | `customer`, `customer_transaction`, `transaction_item` |
| Logistics | `location`, `shipment`, `shipment_item` |

## Design decisions

- **Supertype / subtype for products.** Shared fields (`name`, `manufacturer_id`) live in `product`. Type-specific fields live in `pet_food`, `pet_toy` and `pet_apparel`, whose primary key is also a foreign key to `product`. This avoids sparse NULL columns, and transactions, shipments and animals only need to reference one table.
- **Junction tables for many-to-many relationships** (`product_animal`, `transaction_item`, `shipment_item`) with **composite primary keys**, so the same pair cannot be inserted twice.
- **Two foreign keys to the same table:** `shipment.origin_location_id` and `shipment.destination_location_id` both reference `location`.
- **`customer_transaction` instead of `transaction`,** because `TRANSACTION` is a reserved word in SQL.
- **`zip_code` stored as text** to preserve leading zeros.
- **`quantity` on `transaction_item`**, since a customer may buy several units of the same product.
- **`email` is unique**, so one customer maps to one email address.

## Reflection

- Composite keys vs. surrogate `id` columns in junction tables is a trade-off. A surrogate key is friendlier to ORMs but needs an extra `UNIQUE` constraint to prevent duplicates.
- The rule "each product has **at least one** animal" cannot be enforced by foreign keys alone. It needs application logic or a trigger.
- A `shipment_date` column would be a useful addition.

## How to view

Paste the `.dbml` file into [dbdiagram.io](https://dbdiagram.io/).