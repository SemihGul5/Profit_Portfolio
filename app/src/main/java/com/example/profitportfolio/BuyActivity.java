package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityBuyBinding;
import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class BuyActivity extends AppCompatActivity {
    private ActivityBuyBinding binding;
    int id;
    SQLiteDatabase database;
    DbHelper dbHelper;
    String dateSell,olddateBuy,buyNewDate;
    int pieces;
    double buyPrice,amount,komisyon,topAdet,calcOrt,calcAmount,calcKomisyon,calcTotal;
    double oldsellPrice,oldTotal,oldyuzde,oldbuyPrice,oldKomisyon=0,oldAmount,oldbuyPieces,oldortMaliyet,oldprofitAndLoss;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityBuyBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        setTitle("Satın Al");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
        dbHelper= new DbHelper(this);
        getSelectedData();
        dateBuyTextCalendar();
        updateData();

    }
    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        binding.BuyPiecesText.getText().toString().isEmpty() ||
                        binding.BuyPriceText.getText().toString().isEmpty()||
                                binding.komisyonText.getText().toString().isEmpty() ||
                                binding.BuyDateBuyText.getText().toString().isEmpty()
                )
                {
                    Snackbar.make(v, "Tüm alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    try {
                        calculateAmountAndProfitLoss();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name",binding.BuyStockNameText.getText().toString());
                        contentValues.put("pieces",topAdet);
                        contentValues.put("buyDate",buyNewDate);
                        contentValues.put("sellDate",dateSell);
                        contentValues.put("stockPriceBuy",calcOrt);
                        contentValues.put("stockPriceSell",oldsellPrice);
                        contentValues.put("amount",calcAmount);
                        contentValues.put("profitAndLoss",oldprofitAndLoss);
                        contentValues.put("komisyon",calcKomisyon);
                        contentValues.put("total",calcTotal);
                        contentValues.put("yuzde",oldyuzde);
                        contentValues.put("ortMaliyet",calcOrt);

                        database = dbHelper.getWritableDatabase();
                        long l= database.update(TABLENAME,contentValues,"id="+id,null);

                        if (l != -1) {
                            Toast.makeText(BuyActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(BuyActivity.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BuyActivity.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void calculateAmountAndProfitLoss() {
        try {

            komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
            buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
            pieces = Integer.parseInt(binding.BuyPiecesText.getText().toString());
            buyNewDate = binding.BuyDateBuyText.getText().toString();

            topAdet=pieces+oldbuyPieces;
            amount=(buyPrice*pieces)+komisyon;
            calcAmount=amount+oldAmount;
            calcKomisyon=komisyon+oldKomisyon;
            calcTotal=oldTotal+amount;
            calcOrt=(oldAmount+(amount))/topAdet;


            binding.amountText.setText(String.format("%.2f", calcAmount));
            binding.hesaplananText.setText(String.format("%.2f", calcOrt));

        } catch (Exception e) {
            Toast.makeText(BuyActivity.this, "Hesaplama sırasında bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void getSelectedData() {
        Bundle bundle = getIntent().getBundleExtra("userData");
        if (bundle != null) {
            Log.d("BuyActivity", "Bundle içeriği: " + bundle.toString());
            id = bundle.getInt("id");
            binding.BuyStockNameText.setText(bundle.getString("name"));
            oldbuyPieces = bundle.getInt("pieces");
            olddateBuy = bundle.getString("buyDate");
            dateSell = bundle.getString("sellDate");
            oldbuyPrice = bundle.getDouble("stockPriceBuy");
            oldsellPrice = bundle.getDouble("stockPriceSell");
            oldAmount = bundle.getDouble("amount");
            oldprofitAndLoss = bundle.getDouble("profitAndLoss");
            oldKomisyon = bundle.getDouble("komisyon");
            oldTotal = bundle.getDouble("total");
            oldyuzde = bundle.getDouble("yuzde");
            oldortMaliyet = bundle.getDouble("ortMaliyet");
        } else {

            Toast.makeText(this, "Veri alınamadı. Hata!", Toast.LENGTH_SHORT).show();
        }
    }
    private void dateBuyTextCalendar() {
        binding.BuyDateBuyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BuyActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                binding.BuyDateBuyText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

}