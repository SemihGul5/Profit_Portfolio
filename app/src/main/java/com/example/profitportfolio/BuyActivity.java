package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
        updateData();



    }
    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        binding.BuyPiecesText.getText().toString().isEmpty() ||
                        binding.BuyPriceText.getText().toString().isEmpty()
                )
                {
                    Snackbar.make(v, "Gerekli alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    try {
                        calculateAmountAndProfitLoss();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", binding.BuyStockNameText.getText().toString());
                        contentValues.put("pieces", binding.BuyPiecesText.getText().toString());
                        contentValues.put("buyDate", binding.dateBuyText.getText().toString());
                        contentValues.put("sellDate", binding.dateSellText.getText().toString());
                        contentValues.put("stockPriceBuy", binding.BuyPriceText.getText().toString());
                        contentValues.put("stockPriceSell", binding.sellPriceText.getText().toString());
                        contentValues.put("amount", binding.amountText.getText().toString());
                        contentValues.put("profitAndLoss", binding.profitLossText.getText().toString());
                        contentValues.put("komisyon", binding.komisyonText.getText().toString());
                        contentValues.put("total", binding.totalAmountText.getText().toString());
                        contentValues.put("yuzde", binding.yuzdeText.getText().toString());

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
            double sellPrice;
            double komisyon ;
            int pieces;
            double amount;
            double profitLoss;
            double totalAmount;
            double buyPrice;
            double yuzde;
            String color="";
            if (!binding.sellPriceText.getText().toString().isEmpty()&&!binding.komisyonText.getText().toString().isEmpty()) {
                sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.BuyPiecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = (sellPrice * pieces) - amount - komisyon;
                totalAmount = amount + profitLoss;
                yuzde=(profitLoss/amount)*100;
            }
            else if(!binding.sellPriceText.getText().toString().isEmpty()&&binding.komisyonText.getText().toString().isEmpty()){
                sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
                komisyon = 0;
                buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.BuyPiecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = (sellPrice * pieces) - amount - komisyon;
                totalAmount = amount + profitLoss;
                yuzde=(profitLoss/amount)*100;
            }
            else if(binding.sellPriceText.getText().toString().isEmpty()&&!binding.komisyonText.getText().toString().isEmpty()){
                sellPrice = 0;
                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.BuyPiecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = amount-komisyon;
                totalAmount = amount-komisyon;
                yuzde=0;
            }
            else{
                sellPrice = 0;
                komisyon = 0;
                buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.BuyPiecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = 0;
                totalAmount = amount;
                yuzde=0;
            }



            String resultProfitLoss = "";
            if (profitLoss < 0) {
                resultProfitLoss = "-";
                color = "red";
            } else if (profitLoss > 0) {
                resultProfitLoss = "+";
                color = "green";
            } else {
                resultProfitLoss = "";
            }

            binding.amountText.setText(String.format("%.2f", amount));
            binding.profitLossText.setText(String.format("%.2f", profitLoss));
            binding.profitLossText.setTextColor(getResources().getColor(
                    resultProfitLoss.equals("+") ? R.color.green : (resultProfitLoss.equals("-") ? R.color.red : R.color.black)
            ));
            binding.totalAmountText.setText(String.format("%.2f", totalAmount));
            binding.totalAmountText.setTextColor(getResources().getColor(
                    resultProfitLoss.equals("+") ? R.color.green : (resultProfitLoss.equals("-") ? R.color.red : R.color.black)
            ));
            binding.yuzdeText.setText(String.format("%.2f", yuzde) );



            if (yuzde > 0) {
                binding.yuzdeText.setTextColor(getResources().getColor(R.color.green));

            } else if (yuzde < 0) {
                binding.yuzdeText.setTextColor(getResources().getColor(R.color.red));

            } else {
                binding.yuzdeText.setTextColor(getResources().getColor(R.color.black));

            }

        }
        catch (Exception e) {
            Toast.makeText(BuyActivity.this,"hata",Toast.LENGTH_SHORT).show();
        }

    }
    private void getSelectedData() {
        Bundle bundle= getIntent().getBundleExtra("userData");
        binding.oldAmountText.setText(bundle.getString("total"));
        id=bundle.getInt("id");
    }

}