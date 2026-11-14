import sqlite3

conn = sqlite3.connect("grocery.db")
conn.execute("""CREATE TABLE IF NOT EXISTS products(
  id INTEGER PRIMARY KEY, name TEXT, price REAL, stock INTEGER)""")
conn.execute("""CREATE TABLE IF NOT EXISTS sales(
  id INTEGER PRIMARY KEY, total REAL, created TEXT DEFAULT CURRENT_TIMESTAMP)""")
conn.commit()
