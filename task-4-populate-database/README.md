# Task 4 – Populate the Shipment Database

## Problem

Write a Python script that reads the shipping department's spreadsheets and loads them into an existing SQLite database.

| File | Contents | Handling |
|------|----------|----------|
| `shipping_data_0.csv` | One shipment line per row, with quantity | Inserted directly |
| `shipping_data_1.csv` | One **unit** of a product per row, keyed by shipment ID | Grouped by (shipment, product), and the row count becomes the quantity |
| `shipping_data_2.csv` | Origin and destination per shipment ID | Joined with file 1 |

### Database schema

```sql
product  (id, name UNIQUE)
shipment (id, product_id → product.id, quantity, origin, destination)
```

## Approach

1. Build a lookup `shipment_identifier → (origin, destination)` from file 2.
2. Count rows in file 1 per `(shipment_identifier, product)` with `collections.Counter`.
3. Insert each product once (`INSERT OR IGNORE`) and cache its `id`.
4. Insert one `shipment` row per product per shipment.

## Design decisions

- **Single transaction:** everything is committed once at the end and rolled back on error, so the database is never left half-populated.
- **`csv.DictReader`:** columns are accessed by name rather than by position, so a reordered column does not silently write wrong data.
- **Product id cache:** avoids repeating the same `SELECT` for every row.
- **Parameterized queries (`?`):** protect against SQL injection.

## How to run

```bash
cd task-4-populate-database
python3 populate_database.py
```

Run the script **once**. A second run would insert the shipments again. To restore the original database: `git checkout shipment_database.db`.

## Result

```bash
sqlite3 shipment_database.db "SELECT COUNT(*) FROM product; SELECT COUNT(*) FROM shipment;"
# 45
# 154
```

154 = 110 rows from file 0 + 44 grouped (shipment, product) pairs from file 1.

## Possible improvements

- Accept the database and data paths as command-line arguments (`argparse`) instead of constants.
- Make the script idempotent, so that running it twice has the same effect as running it once.

---

*Data source: [theforage/forage-walmart-task-4](https://github.com/theforage/forage-walmart-task-4)*