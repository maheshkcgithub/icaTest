import tkinter as tk
from tkinter import ttk, messagebox
from db import conn

cart = {}  # product_id -> qty


def load_products():
    tree.delete(*tree.get_children())
    for row in conn.execute("SELECT id, name, price, stock FROM products"):
        tree.insert("", "end", values=row)


def add_product():
    try:
        name = name_e.get().strip()
        price, stock = float(price_e.get()), int(stock_e.get())
        if not name:
            raise ValueError
    except ValueError:
        return messagebox.showerror("Error", "Enter a name, a numeric price and a whole-number stock")
    conn.execute("INSERT INTO products(name, price, stock) VALUES(?,?,?)", (name, price, stock))
    conn.commit()
    for e in (name_e, price_e, stock_e):
        e.delete(0, "end")
    load_products()


def add_to_cart():
    sel = tree.selection()
    if not sel:
        return
    pid, name, price, stock = tree.item(sel[0])["values"]
    if cart.get(pid, 0) >= int(stock):
        return messagebox.showwarning("Stock", "Not enough stock")
    cart[pid] = cart.get(pid, 0) + 1
    refresh_cart()


def refresh_cart():
    cart_list.delete(0, "end")
    total = 0
    for pid, qty in cart.items():
        name, price = conn.execute("SELECT name, price FROM products WHERE id=?", (pid,)).fetchone()
        cart_list.insert("end", f"{name} x{qty} = {price * qty:.2f}")
        total += price * qty
    total_lbl.config(text=f"Total: {total:.2f}")
    return total


def checkout():
    if not cart:
        return
    total = refresh_cart()
    for pid, qty in cart.items():
        conn.execute("UPDATE products SET stock = stock - ? WHERE id=?", (qty, pid))
    conn.execute("INSERT INTO sales(total) VALUES(?)", (total,))
    conn.commit()
    cart.clear()
    refresh_cart()
    load_products()
    messagebox.showinfo("Done", f"Sale complete: {total:.2f}")


root = tk.Tk()
root.title("Grocery Store")

form = tk.Frame(root)
form.pack(pady=5)
name_e, price_e, stock_e = (tk.Entry(form, width=12) for _ in range(3))
for lbl, e in [("Name", name_e), ("Price", price_e), ("Stock", stock_e)]:
    tk.Label(form, text=lbl).pack(side="left")
    e.pack(side="left")
tk.Button(form, text="Add Product", command=add_product).pack(side="left", padx=5)

tree = ttk.Treeview(root, columns=("ID", "Name", "Price", "Stock"), show="headings")
for c in ("ID", "Name", "Price", "Stock"):
    tree.heading(c, text=c)
tree.pack(fill="both", expand=True)
tk.Button(root, text="Add to Cart", command=add_to_cart).pack(pady=5)

cart_list = tk.Listbox(root, height=6)
cart_list.pack(fill="x")
total_lbl = tk.Label(root, text="Total: 0.00", font=("Arial", 14))
total_lbl.pack()
tk.Button(root, text="Checkout", command=checkout).pack(pady=5)

load_products()
root.mainloop()
