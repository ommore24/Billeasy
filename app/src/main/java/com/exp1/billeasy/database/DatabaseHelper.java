package com.exp1.billeasy.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.exp1.billeasy.models.Product;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_PREFIX = "Billeasy_";
    private static final int DATABASE_VERSION = 5;
    private final Context context;

    // A helper method to determine the correct database name for the current session
    private static String getDynamicDatabaseName(Context context) {
        android.content.SharedPreferences prefs = context.getSharedPreferences("BillEasyPrefs", Context.MODE_PRIVATE);
        // Default to 'global' for login/register when no user is logged in yet
        String user = prefs.getString("logged_in_user", "global");
        return DATABASE_PREFIX + user + ".db";
    }

    public DatabaseHelper(Context context) {
        super(context, getDynamicDatabaseName(context), null, DATABASE_VERSION);
        this.context = context;
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
                        "supplier TEXT, " +
                        "image_path TEXT" +
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

        db.execSQL(
                "CREATE TABLE StoreProfile (" +
                        "id INTEGER PRIMARY KEY, " +
                        "store_name TEXT, " +
                        "owner_name TEXT, " +
                        "phone TEXT, " +
                        "address TEXT, " +
                        "gst_number TEXT)"
        );

        // Insert initial empty profile for this specific user's database
        db.execSQL("INSERT INTO StoreProfile (id, store_name) VALUES (1, 'BillEasy Store')");
    }


    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {
        // Upgrade logic for user-specific database
    }


    // =========================================================================
    // AUTHENTICATION METHODS (Uses a shared Global Auth Database)
    // =========================================================================

    private SQLiteDatabase getAuthDatabase(boolean writable) {
        // We open a central specific file for Users so all sessions share the same accounts
        SQLiteDatabase authDb = context.openOrCreateDatabase("Billeasy_Auth.db", Context.MODE_PRIVATE, null);
        
        // Ensure Users table exists in the global auth file
        authDb.execSQL("CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT)");
        
        // Ensure default admin exists
        authDb.execSQL("INSERT OR IGNORE INTO Users (username, password) VALUES ('admin', 'admin123')");
        
        return authDb;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = getAuthDatabase(false);
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Users WHERE username=? AND password=?",
                new String[]{username, password}
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = getAuthDatabase(true);
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("Users", null, values);
        db.close();
        return result != -1;
    }


    // =========================================================================
    // INVENTORY METHODS (Automatically uses the logged-in user's database)
    // =========================================================================

    public boolean addProduct(String name,
                              String category,
                              double price,
                              int quantity,
                              String supplier,
                              String imagePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("supplier", supplier);
        values.put("image_path", imagePath);

        long result = db.insert("Products", null, values);

        db.close();

        return result != -1;
    }


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

                String imagePath = cursor.getString(
                        cursor.getColumnIndexOrThrow("image_path")
                );

                productList.add(
                        new Product(
                                id,
                                name,
                                category,
                                price,
                                quantity,
                                supplier,
                                imagePath
                        )
                );

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return productList;
    }


    public Product getProduct(int productId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM Products WHERE id=?",
                new String[]{String.valueOf(productId)}
        );

        Product product = null;

        if (cursor.moveToFirst()) {

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

            String imagePath = cursor.getString(
                    cursor.getColumnIndexOrThrow("image_path")
            );

            product = new Product(
                    id,
                    name,
                    category,
                    price,
                    quantity,
                    supplier,
                    imagePath
            );
        }

        cursor.close();
        db.close();

        return product;
    }


    public boolean updateProduct(
            int id,
            String name,
            String category,
            double price,
            int quantity,
            String supplier,
            String imagePath) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("supplier", supplier);
        values.put("image_path", imagePath);

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


    public boolean addStock(int productId, int addedQuantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT quantity FROM Products WHERE id=?",
                new String[]{String.valueOf(productId)}
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return false;
        }

        int currentStock = cursor.getInt(0);
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("quantity", currentStock + addedQuantity);

        int result = db.update(
                "Products",
                values,
                "id=?",
                new String[]{String.valueOf(productId)}
        );

        db.close();

        return result > 0;
    }


    // =========================================================================
    // BILLING METHODS
    // =========================================================================

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

    public Cursor getAllBills() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Bills ORDER BY id DESC",
                null
        );
    }

    public Cursor getRecentBills(int limit) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Bills " +
                        "ORDER BY id DESC LIMIT ?",
                new String[]{
                        String.valueOf(limit)
                }
        );
    }

    public boolean deleteBill(int billId) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        // Delete bill items first
        db.delete(
                "BillItems",
                "bill_id=?",
                new String[]{
                        String.valueOf(billId)
                }
        );

        // Delete bill
        int result =
                db.delete(
                        "Bills",
                        "id=?",
                        new String[]{
                                String.valueOf(billId)
                        }
                );

        return result > 0;
    }

    public Cursor getBill(int billId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM Bills WHERE id=?",
                new String[]{
                        String.valueOf(billId)
                }
        );
    }

    public Cursor getBillItems(int billId) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM BillItems WHERE bill_id=?",
                new String[]{
                        String.valueOf(billId)
                }
        );
    }


    // =========================================================================
    // ANALYTICS & DASHBOARD METHODS
    // =========================================================================

    public int getProductCount() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Products",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public int getLowStockCount() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Products WHERE quantity <= 5",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    public double getTodayRevenue() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        double revenue = 0;

        Cursor cursor = db.rawQuery(
                "SELECT total, date FROM Bills",
                null
        );

        String today =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                ).format(new Date());

        if (cursor.moveToFirst()) {

            do {

                double total =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("total")
                        );

                String billDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("date")
                        );

                if (billDate != null &&
                        billDate.startsWith(today)) {

                    revenue += total;
                }

            } while (cursor.moveToNext());
        }

        cursor.close();

        return revenue;
    }

    public double getYesterdayRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        double revenue = 0;

        Cursor cursor = db.rawQuery("SELECT total, date FROM Bills", null);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String yesterday = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.getTime());

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                    String billDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                    if (billDate != null && billDate.startsWith(yesterday)) {
                        revenue += total;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return revenue;
    }

    public static class AnalyticsData {
        public double totalSales = 0.0;
        public int totalProductsSold = 0;
        public int totalBills = 0;
        public double avgBillValue = 0.0;
        public double totalDiscount = 0.0;
        public double totalGst = 0.0;
        public String topSellingProduct = "N/A";
        public int topSellingQuantity = 0;
    }

    public AnalyticsData getAnalyticsData(String singleDateStr, Date startDate, Date endDate) {
        AnalyticsData data = new AnalyticsData();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, total, discount, gst, date FROM Bills", null);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Map<String, Integer> productQtyMap = new HashMap<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int billId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                    double discount = cursor.getDouble(cursor.getColumnIndexOrThrow("discount"));
                    double gst = cursor.getDouble(cursor.getColumnIndexOrThrow("gst"));
                    String billDateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                    boolean matches = false;

                    if (singleDateStr != null && !singleDateStr.isEmpty()) {
                        if (billDateStr != null && billDateStr.startsWith(singleDateStr)) {
                            matches = true;
                        }
                    } else if (startDate != null && endDate != null) {
                        if (billDateStr != null) {
                            try {
                                String datePart = billDateStr;
                                if (billDateStr.contains(",")) {
                                    datePart = billDateStr.split(",")[0].trim();
                                }
                                Date parsedDate = sdf.parse(datePart);
                                if (parsedDate != null && !parsedDate.before(startDate) && !parsedDate.after(endDate)) {
                                    matches = true;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    } else {
                        // All time
                        matches = true;
                    }

                    if (matches) {
                        data.totalSales += total;
                        data.totalDiscount += discount;
                        data.totalGst += gst;
                        data.totalBills++;

                        Cursor itemCursor = db.rawQuery("SELECT product_name, quantity FROM BillItems WHERE bill_id=?", new String[]{String.valueOf(billId)});
                        if (itemCursor != null) {
                            if (itemCursor.moveToFirst()) {
                                do {
                                    String pName = itemCursor.getString(itemCursor.getColumnIndexOrThrow("product_name"));
                                    int qty = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"));
                                    data.totalProductsSold += qty;
                                    if (pName != null && !pName.isEmpty()) {
                                        Integer currentQty = productQtyMap.get(pName);
                                    productQtyMap.put(pName, (currentQty == null ? 0 : currentQty) + qty);
                                    }
                                } while (itemCursor.moveToNext());
                            }
                            itemCursor.close();
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        if (data.totalBills > 0) {
            data.avgBillValue = data.totalSales / data.totalBills;
        }

        String topProduct = null;
        int maxQty = 0;
        for (Map.Entry<String, Integer> entry : productQtyMap.entrySet()) {
            if (entry.getValue() > maxQty) {
                maxQty = entry.getValue();
                topProduct = entry.getKey();
            }
        }

        if (topProduct != null && maxQty > 0) {
            data.topSellingProduct = topProduct + " (" + maxQty + " sold)";
            data.topSellingQuantity = maxQty;
        }

        return data;
    }


    // =========================================================================
    // PROFILE METHODS
    // =========================================================================

    public boolean updateProfile(String storeName, String ownerName, String phone, String address, String gst) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("store_name", storeName);
        values.put("owner_name", ownerName);
        values.put("phone", phone);
        values.put("address", address);
        values.put("gst_number", gst);

        int result = db.update("StoreProfile", values, "id=1", null);
        db.close();
        return result > 0;
    }

    public Cursor getStoreProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM StoreProfile WHERE id=1", null);
    }
}