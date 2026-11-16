import csv
import sqlite3
from collections import Counter

DATABASE_PATH = "shipment_database.db"
SPREADSHEET_0 = "data/shipping_data_0.csv"
SPREADSHEET_1 = "data/shipping_data_1.csv"
SPREADSHEET_2 = "data/shipping_data_2.csv"


def read_rows(path):
    """Read a CSV file and return its rows as a list of dictionaries."""
    with open(path, newline="", encoding="utf-8") as csv_file:
        return list(csv.DictReader(csv_file))


def get_product_id(cursor, product_name, product_cache):
    """Return the id of a product, inserting the product first if it does not exist."""
    if product_name in product_cache:
        return product_cache[product_name]
    cursor.execute("INSERT OR IGNORE INTO product (name) VALUES (?)", (product_name,))
    cursor.execute("SELECT id FROM product WHERE name = ?", (product_name,))
    product_id = cursor.fetchone()[0]
    product_cache[product_name] = product_id
    return product_id


def insert_shipment(cursor, product_id, quantity, origin, destination):
    cursor.execute(
        "INSERT INTO shipment (product_id, quantity, origin, destination) VALUES (?, ?, ?, ?)",
        (product_id, quantity, origin, destination),
    )


def populate_from_spreadsheet_0(cursor, product_cache):
    """Spreadsheet 0 is self contained: one row = one shipment row."""
    for row in read_rows(SPREADSHEET_0):
        product_id = get_product_id(cursor, row["product"], product_cache)
        insert_shipment(
            cursor,
            product_id,
            int(row["product_quantity"]),
            row["origin_warehouse"],
            row["destination_store"],
        )


def populate_from_spreadsheets_1_and_2(cursor, product_cache):
    """Spreadsheet 1 lists one product per row; spreadsheet 2 holds origin/destination."""
    # shipment_identifier -> (origin, destination)
    locations = {
        row["shipment_identifier"]: (row["origin_warehouse"], row["destination_store"])
        for row in read_rows(SPREADSHEET_2)
    }

    # (shipment_identifier, product) -> number of rows = quantity
    quantities = Counter(
        (row["shipment_identifier"], row["product"]) for row in read_rows(SPREADSHEET_1)
    )

    for (shipment_identifier, product_name), quantity in quantities.items():
        origin, destination = locations[shipment_identifier]
        product_id = get_product_id(cursor, product_name, product_cache)
        insert_shipment(cursor, product_id, quantity, origin, destination)


def main():
    connection = sqlite3.connect(DATABASE_PATH)
    try:
        cursor = connection.cursor()
        product_cache = {}
        populate_from_spreadsheet_0(cursor, product_cache)
        populate_from_spreadsheets_1_and_2(cursor, product_cache)
        connection.commit()
        print("Database populated successfully.")
    except Exception:
        connection.rollback()
        raise
    finally:
        connection.close()


if __name__ == "__main__":
    main()