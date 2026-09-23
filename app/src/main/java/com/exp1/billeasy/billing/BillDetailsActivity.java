package com.exp1.billeasy.billing;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exp1.billeasy.R;
import com.exp1.billeasy.adapters.BillItemAdapter;
import com.exp1.billeasy.database.DatabaseHelper;
import com.exp1.billeasy.models.BillItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class BillDetailsActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    TextView tvBillNumber;
    TextView tvCustomer;
    TextView tvDate;
    TextView tvSubtotal;
    TextView tvDiscount;
    TextView tvGst;
    TextView tvGrandTotal;
    MaterialButton btnGenerateReceipt;

    RecyclerView recyclerBillItems;

    DatabaseHelper databaseHelper;

    ArrayList<BillItem> billItems = new ArrayList<>();

    BillItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_details);

        btnBack = findViewById(R.id.btnBack);
        tvBillNumber = findViewById(R.id.tvBillNumber);
        tvCustomer = findViewById(R.id.tvCustomer);
        tvDate = findViewById(R.id.tvDate);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvGst = findViewById(R.id.tvGst);
        tvGrandTotal = findViewById(R.id.tvGrandTotal);
        btnGenerateReceipt = findViewById(R.id.btnGenerateReceipt);
        recyclerBillItems = findViewById(R.id.recyclerBillItems);

        databaseHelper = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());

        int billId = getIntent().getIntExtra("bill_id", -1);

        if (billId == -1) {
            finish();
            return;
        }

        recyclerBillItems.setLayoutManager(new LinearLayoutManager(this));

        loadBill(billId);
        loadBillItems(billId);

        btnGenerateReceipt.setOnClickListener(v -> generatePdfReceipt(billId));
    }


    private void loadBill(int billId) {
        Cursor cursor = databaseHelper.getBill(billId);

        if (cursor.moveToFirst()) {
            double subtotal = cursor.getDouble(cursor.getColumnIndexOrThrow("subtotal"));
            double discount = cursor.getDouble(cursor.getColumnIndexOrThrow("discount"));
            double gst = cursor.getDouble(cursor.getColumnIndexOrThrow("gst"));
            double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            String customer = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

            tvBillNumber.setText("BILL #" + billId);
            tvCustomer.setText(customer == null || customer.trim().isEmpty() ? "Walk-in Customer" : customer);
            tvDate.setText(date);
            tvSubtotal.setText("Subtotal     ₹" + format(subtotal));
            tvDiscount.setText("Discount     ₹" + format(discount));
            tvGst.setText("GST           ₹" + format(gst));
            tvGrandTotal.setText("₹" + format(total));
        }
        cursor.close();
    }


    private void loadBillItems(int billId) {
        billItems.clear();

        Cursor cursor = databaseHelper.getBillItems(billId);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("product_name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));

                billItems.add(new BillItem(id, name, price, quantity, total));
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new BillItemAdapter(billItems);
        recyclerBillItems.setAdapter(adapter);
    }


    private void generatePdfReceipt(int billId) {
        // Fetch store profile data dynamically from Phase 4
        String storeName = "BillEasy Store";
        String ownerName = "";
        String phone = "";
        String gstNumber = "";
        String address = "";

        Cursor storeCursor = databaseHelper.getStoreProfile();
        if (storeCursor != null && storeCursor.moveToFirst()) {
            String dbStore = storeCursor.getString(storeCursor.getColumnIndexOrThrow("store_name"));
            String dbOwner = storeCursor.getString(storeCursor.getColumnIndexOrThrow("owner_name"));
            String dbPhone = storeCursor.getString(storeCursor.getColumnIndexOrThrow("phone"));
            String dbGst = storeCursor.getString(storeCursor.getColumnIndexOrThrow("gst_number"));
            String dbAddress = storeCursor.getString(storeCursor.getColumnIndexOrThrow("address"));

            if (dbStore != null && !dbStore.trim().isEmpty() && !dbStore.equals("Not provided")) storeName = dbStore;
            if (dbOwner != null && !dbOwner.trim().isEmpty() && !dbOwner.equals("Not provided")) ownerName = dbOwner;
            if (dbPhone != null && !dbPhone.trim().isEmpty() && !dbPhone.equals("Not provided")) phone = dbPhone;
            if (dbGst != null && !dbGst.trim().isEmpty() && !dbGst.equals("Not provided")) gstNumber = dbGst;
            if (dbAddress != null && !dbAddress.trim().isEmpty() && !dbAddress.equals("Not provided")) address = dbAddress;
            
            storeCursor.close();
        }

        // Fetch bill details
        String customerName = "Walk-in Customer";
        String billDate = "";
        double subtotal = 0;
        double discount = 0;
        double gst = 0;
        double total = 0;

        Cursor billCursor = databaseHelper.getBill(billId);
        if (billCursor != null && billCursor.moveToFirst()) {
            customerName = billCursor.getString(billCursor.getColumnIndexOrThrow("customer_name"));
            billDate = billCursor.getString(billCursor.getColumnIndexOrThrow("date"));
            subtotal = billCursor.getDouble(billCursor.getColumnIndexOrThrow("subtotal"));
            discount = billCursor.getDouble(billCursor.getColumnIndexOrThrow("discount"));
            gst = billCursor.getDouble(billCursor.getColumnIndexOrThrow("gst"));
            total = billCursor.getDouble(billCursor.getColumnIndexOrThrow("total"));
            billCursor.close();
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            customerName = "Walk-in Customer";
        }

        // Generate A4 Size PDF Document natively
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        
        // Title / Branding Header
        titlePaint.setTextSize(24);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.parseColor("#202B52"));
        canvas.drawText(storeName, 40, 60, titlePaint);
        
        paint.setTextSize(12);
        paint.setColor(Color.DKGRAY);
        int y = 85;
        if (!ownerName.isEmpty()) { canvas.drawText("Owner: " + ownerName, 40, y, paint); y += 18; }
        if (!phone.isEmpty()) { canvas.drawText("Phone: " + phone, 40, y, paint); y += 18; }
        if (!gstNumber.isEmpty()) { canvas.drawText("GSTIN: " + gstNumber, 40, y, paint); y += 18; }
        if (!address.isEmpty()) { canvas.drawText("Address: " + address, 40, y, paint); y += 18; }
        
        // Top separator
        y += 8;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, 555, y, paint);
        
        // Invoice Details section
        y += 25;
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        canvas.drawText("TAX INVOICE", 40, y, paint);
        
        paint.setFakeBoldText(false);
        y += 20;
        canvas.drawText("Bill Number: #" + billId, 40, y, paint);
        canvas.drawText("Date: " + billDate, 360, y, paint);
        y += 18;
        canvas.drawText("Customer: " + customerName, 40, y, paint);
        
        // Middle separator
        y += 15;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, 555, y, paint);
        
        // Table Columns headers
        y += 25;
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        canvas.drawText("Product Item", 40, y, paint);
        canvas.drawText("Rate", 300, y, paint);
        canvas.drawText("Qty", 400, y, paint);
        canvas.drawText("Total", 480, y, paint);
        
        paint.setFakeBoldText(false);
        y += 10;
        canvas.drawLine(40, y, 555, y, paint);
        
        // Iterate through bill items dynamically
        for (BillItem item : billItems) {
            y += 22;
            canvas.drawText(item.getProductName(), 40, y, paint);
            canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", item.getPrice()), 300, y, paint);
            canvas.drawText(String.valueOf(item.getQuantity()), 400, y, paint);
            canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", item.getTotal()), 480, y, paint);
        }
        
        // Lower separator
        y += 15;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(40, y, 555, y, paint);
        
        // Totals Breakdown summary section
        y += 25;
        paint.setColor(Color.DKGRAY);
        canvas.drawText("Subtotal:", 340, y, paint);
        canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", subtotal), 480, y, paint);
        
        y += 18;
        canvas.drawText("Discount:", 340, y, paint);
        canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", discount), 480, y, paint);
        
        y += 18;
        canvas.drawText("GST Amount:", 340, y, paint);
        canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", gst), 480, y, paint);
        
        y += 25;
        paint.setFakeBoldText(true);
        paint.setColor(Color.parseColor("#202B52"));
        paint.setTextSize(14);
        canvas.drawText("Grand Total:", 340, y, paint);
        canvas.drawText("₹" + String.format(Locale.getDefault(), "%.2f", total), 480, y, paint);
        
        // Professional Thank-you footer
        y += 60;
        paint.setFakeBoldText(false);
        paint.setColor(Color.GRAY);
        paint.setTextSize(13);
        canvas.drawText("Thank you for your business!", 210, y, paint);
        
        pdfDocument.finishPage(page);
        
        // Save PDF file to secure application cache directory
        try {
            File file = new File(getCacheDir(), "BillEasy_Receipt_" + billId + ".pdf");
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            fos.close();
            pdfDocument.close();
            
            // Generate content URI using FileProvider configured in Phase 5
            Uri uri = FileProvider.getUriForFile(
                    this,
                    "com.exp1.billeasy.fileprovider",
                    file
            );
            
            // Create a universal Action Send chooser sheet (allows printing, saving to drive, or chatting)
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Share or Save Receipt"));
            
        } catch (Exception e) {
            Toast.makeText(this, "Error generating receipt: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private String format(double value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}