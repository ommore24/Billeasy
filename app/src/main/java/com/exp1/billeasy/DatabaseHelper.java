package com.exp1.billeasy;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Billeasy.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createProductsTable =
                "CREATE TABLE Products (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "category TEXT, " +
                        "price REAL NOT NULL, " +
                        "quantity INTEGER NOT NULL, " +
                        "supplier TEXT" +
                        ")";

        db.execSQL(createProductsTable);
        db.execSQL(
                "CREATE TABLE Bills (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "customer_name TEXT, " +
                        "subtotal REAL, " +
                        "discount REAL, " +
                        "gst REAL, " +
                        "total REAL, " +
                        "date TEXT)"
        );

        db.execSQL(
                "CREATE TABLE BillItems (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "bill_id INTEGER, " +
                        "product_id INTEGER, " +
                        "product_name TEXT, " +
                        "price REAL, " +
                        "quantity INTEGER, " +
                        "total REAL)"
        );
    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 2) {

            db.execSQL(
                    "CREATE TABLE Bills (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "customer_name TEXT, " +
                            "subtotal REAL, " +
                            "discount REAL, " +
                            "gst REAL, " +
                            "total REAL, " +
                            "date TEXT)"
            );

            db.execSQL(
                    "CREATE TABLE BillItems (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "bill_id INTEGER, " +
                            "product_id INTEGER, " +
                            "product_name TEXT, " +
                            "price REAL, " +
                            "quantity INTEGER, " +
                            "total REAL)"
            );
        }
    }


    // ADD PRODUCT

    public boolean addProduct(String name,
                              String category,
                              double price,
                              int quantity,
                              String supplier) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("supplier", supplier);

        long result = db.insert("Products", null, values);

        db.close();

        return result != -1;
    }


    // GET ALL PRODUCTS

    public ArrayList<Product> getAllProducts() {

        ArrayList<Product> productList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Products ORDER BY id DESC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category")
                );

                double price = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("price")
                );

                int quantity = cursor.getInt(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String supplier = cursor.getString(
                        cursor.getColumnIndexOrThrow("supplier")
                );

                productList.add(
                        new Product(
                                id,
                                name,
                                category,
                                price,
                                quantity,
                                supplier
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return productList;
    }
    public boolean updateProduct(
            int id,
            String name,
            String category,
            double price,
            int quantity,
            String supplier) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("supplier", supplier);

        int result = db.update(
                "Products",
                values,
                "id=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return result > 0;
    }


    public boolean deleteProduct(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                "Products",
                "id=?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return result > 0;
    }
    public long createBill(
            String customerName,
            double subtotal,
            double discount,
            double gst,
            double total,
            String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("customer_name", customerName);
        values.put("subtotal", subtotal);
        values.put("discount", discount);
        values.put("gst", gst);
        values.put("total", total);
        values.put("date", date);

        return db.insert("Bills", null, values);
    }
    public boolean addBillItem(
            long billId,
            int productId,
            String productName,
            double price,
            int quantity,
            double total) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("bill_id", billId);
        values.put("product_id", productId);
        values.put("product_name", productName);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("total", total);

        long result = db.insert(
                "BillItems",
                null,
                values
        );

        return result != -1;
    }
    public boolean reduceStock(
            int productId,
            int quantity) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT quantity FROM Products WHERE id=?",
                new String[]{
                        String.valueOf(productId)
                }
        );

        if (!cursor.moveToFirst()) {

            cursor.close();
            db.close();

            return false;
        }

        int currentStock =
                cursor.getInt(0);

        cursor.close();

        int newStock =
                currentStock - quantity;

        if (newStock < 0) {

            db.close();

            return false;
        }

        ContentValues values =
                new ContentValues();

        values.put(
                "quantity",
                newStock
        );

        int result =
                db.update(
                        "Products",
                        values,
                        "id=?",
                        new String[]{
                                String.valueOf(productId)
                        }
                );

        db.close();

        return result > 0;
    }
    // GET ALL BILLS

    public Cursor getAllBills() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Bills ORDER BY id DESC",
                null
        );
    }


// GET SINGLE BILL

    public Cursor getBill(int billId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Bills WHERE id=?",
                new String[]{
                        String.valueOf(billId)
                }
        );
    }


// GET BILL ITEMS

    public Cursor getBillItems(int billId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM BillItems WHERE bill_id=?",
                new String[]{
                        String.valueOf(billId)
                }
        );
    }

}