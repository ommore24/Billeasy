package com.exp1.billeasy.analytics;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exp1.billeasy.R;
import com.exp1.billeasy.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {

    MaterialCardView btnBack;
    ChipGroup chipGroupFilter;
    Chip chipAllTime, chipToday, chipThisWeek, chipThisMonth;
    MaterialButton btnPickSingleDate, btnPickDateRange;
    TextView tvActiveFilterText, btnResetFilter;

    TextView tvAnalyticsTotalSales;
    TextView tvAnalyticsProductsSold;
    TextView tvAnalyticsTotalBills;
    TextView tvAnalyticsAvgBillValue;
    TextView tvAnalyticsTopProduct;
    TextView tvAnalyticsTotalDiscount;
    TextView tvAnalyticsTotalGst;

    DatabaseHelper db;

    private String selectedFilterMode = "ALL_TIME";
    private String singleDateFilter = null;
    private Date startDateFilter = null;
    private Date endDateFilter = null;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        db = new DatabaseHelper(this);

        // Header
        btnBack = findViewById(R.id.btnBack);

        // Filter Controls
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        chipAllTime = findViewById(R.id.chipAllTime);
        chipToday = findViewById(R.id.chipToday);
        chipThisWeek = findViewById(R.id.chipThisWeek);
        chipThisMonth = findViewById(R.id.chipThisMonth);

        btnPickSingleDate = findViewById(R.id.btnPickSingleDate);
        btnPickDateRange = findViewById(R.id.btnPickDateRange);
        tvActiveFilterText = findViewById(R.id.tvActiveFilterText);
        btnResetFilter = findViewById(R.id.btnResetFilter);

        // Metrics TextViews
        tvAnalyticsTotalSales = findViewById(R.id.tvAnalyticsTotalSales);
        tvAnalyticsProductsSold = findViewById(R.id.tvAnalyticsProductsSold);
        tvAnalyticsTotalBills = findViewById(R.id.tvAnalyticsTotalBills);
        tvAnalyticsAvgBillValue = findViewById(R.id.tvAnalyticsAvgBillValue);
        tvAnalyticsTopProduct = findViewById(R.id.tvAnalyticsTopProduct);
        tvAnalyticsTotalDiscount = findViewById(R.id.tvAnalyticsTotalDiscount);
        tvAnalyticsTotalGst = findViewById(R.id.tvAnalyticsTotalGst);

        btnBack.setOnClickListener(v -> finish());

        // Set preset chip listeners
        chipAllTime.setOnClickListener(v -> {
            selectedFilterMode = "ALL_TIME";
            singleDateFilter = null;
            startDateFilter = null;
            endDateFilter = null;
            tvActiveFilterText.setText("Showing: All Time");
            loadMetrics();
        });

        chipToday.setOnClickListener(v -> {
            selectedFilterMode = "TODAY";
            singleDateFilter = dateFormat.format(new Date());
            startDateFilter = null;
            endDateFilter = null;
            tvActiveFilterText.setText("Showing: Today (" + singleDateFilter + ")");
            loadMetrics();
        });

        chipThisWeek.setOnClickListener(v -> {
            selectedFilterMode = "THIS_WEEK";
            singleDateFilter = null;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            endDateFilter = cal.getTime();

            cal.add(Calendar.DAY_OF_YEAR, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startDateFilter = cal.getTime();

            tvActiveFilterText.setText("Showing: Past 7 Days");
            loadMetrics();
        });

        chipThisMonth.setOnClickListener(v -> {
            selectedFilterMode = "THIS_MONTH";
            singleDateFilter = null;

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            endDateFilter = cal.getTime();

            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startDateFilter = cal.getTime();

            tvActiveFilterText.setText("Showing: This Month");
            loadMetrics();
        });

        btnPickSingleDate.setOnClickListener(v -> showSingleDatePicker());

        btnPickDateRange.setOnClickListener(v -> showDateRangePicker());

        btnResetFilter.setOnClickListener(v -> {
            chipAllTime.setChecked(true);
            selectedFilterMode = "ALL_TIME";
            singleDateFilter = null;
            startDateFilter = null;
            endDateFilter = null;
            tvActiveFilterText.setText("Showing: All Time");
            loadMetrics();
        });

        // Default Load
        chipAllTime.setChecked(true);
        loadMetrics();
    }

    private void showSingleDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth);

            singleDateFilter = dateFormat.format(selectedCal.getTime());
            selectedFilterMode = "SINGLE_DATE";
            startDateFilter = null;
            endDateFilter = null;

            if (chipGroupFilter != null) {
                chipGroupFilter.clearCheck();
            }

            tvActiveFilterText.setText("Showing: " + singleDateFilter);
            loadMetrics();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        dialog.setTitle("Select Date");
        dialog.show();
    }

    private void showDateRangePicker() {
        Calendar cal = Calendar.getInstance();

        Toast.makeText(this, "Select Start Date", Toast.LENGTH_SHORT).show();

        DatePickerDialog startDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar startCal = Calendar.getInstance();
            startCal.set(year, month, dayOfMonth, 0, 0, 0);

            DatePickerDialog endDialog = new DatePickerDialog(AnalyticsActivity.this, (view2, year2, month2, dayOfMonth2) -> {
                Calendar endCal = Calendar.getInstance();
                endCal.set(year2, month2, dayOfMonth2, 23, 59, 59);

                if (endCal.before(startCal)) {
                    Toast.makeText(AnalyticsActivity.this, "End date cannot be before start date", Toast.LENGTH_SHORT).show();
                    return;
                }

                startDateFilter = startCal.getTime();
                endDateFilter = endCal.getTime();
                singleDateFilter = null;
                selectedFilterMode = "DATE_RANGE";

                if (chipGroupFilter != null) {
                    chipGroupFilter.clearCheck();
                }

                tvActiveFilterText.setText("Showing: " + dateFormat.format(startDateFilter) + " - " + dateFormat.format(endDateFilter));
                loadMetrics();
            }, year, month, dayOfMonth);

            endDialog.setTitle("Select End Date");
            endDialog.show();

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        startDialog.setTitle("Select Start Date");
        startDialog.show();
    }

    private void loadMetrics() {
        DatabaseHelper.AnalyticsData data;

        if ("SINGLE_DATE".equals(selectedFilterMode) || "TODAY".equals(selectedFilterMode)) {
            data = db.getAnalyticsData(singleDateFilter, null, null);
        } else if ("THIS_WEEK".equals(selectedFilterMode) || "THIS_MONTH".equals(selectedFilterMode) || "DATE_RANGE".equals(selectedFilterMode)) {
            data = db.getAnalyticsData(null, startDateFilter, endDateFilter);
        } else {
            // ALL_TIME
            data = db.getAnalyticsData(null, null, null);
        }

        tvAnalyticsTotalSales.setText("₹" + String.format(Locale.getDefault(), "%.2f", data.totalSales));
        tvAnalyticsProductsSold.setText(data.totalProductsSold + (data.totalProductsSold == 1 ? " item" : " items"));
        tvAnalyticsTotalBills.setText(String.valueOf(data.totalBills));
        tvAnalyticsAvgBillValue.setText("₹" + String.format(Locale.getDefault(), "%.2f", data.avgBillValue));
        tvAnalyticsTopProduct.setText(data.topSellingProduct);
        tvAnalyticsTotalDiscount.setText("₹" + String.format(Locale.getDefault(), "%.2f", data.totalDiscount));
        tvAnalyticsTotalGst.setText("₹" + String.format(Locale.getDefault(), "%.2f", data.totalGst));
    }
}