package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.example.profitportfolio.databinding.ActivityStockDetailsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class StockDetails extends AppCompatActivity {
    private ActivityStockDetailsBinding binding;
    SQLiteDatabase database;
    DbHelper dbHelper;

    int id;

    double buyPrice;
    int pieces;
    double sellPrice;
    double amount;
    double profitLoss;
    double komisyon;
    double totalAmount;
    public static String color="";
    double yuzde;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStockDetailsBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        binding.saveButton.setText("Güncelle");
        setTitle("Hisse Güncelle");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        visibleAndEnabledGiris();


        dbHelper= new DbHelper(this);

        editData();
        colorControl();
        updateData();
        dateSellTextCalendar();
        dateBuyTextCalendar();

    }

    private void colorControl() {
        double amount,total,prof;
        amount=Double.parseDouble(binding.amountText.getText().toString());
        total=Double.parseDouble(binding.totalAmountText.getText().toString());
        if(total>amount){
            binding.totalAmountText.setTextColor(getResources().getColor(R.color.green));
            binding.profitLossText.setTextColor(getResources().getColor(R.color.green));
            binding.text9.setTextColor(getResources().getColor(R.color.green));
            binding.text10.setTextColor(getResources().getColor(R.color.green));
            binding.text12.setTextColor(getResources().getColor(R.color.green));
            binding.yuzdeText.setTextColor(getResources().getColor(R.color.green));
        }
        else if(total==amount){
            binding.totalAmountText.setTextColor(getResources().getColor(R.color.black));
            binding.profitLossText.setTextColor(getResources().getColor(R.color.black));
            binding.text9.setTextColor(getResources().getColor(R.color.black));
            binding.text10.setTextColor(getResources().getColor(R.color.black));
            binding.text12.setTextColor(getResources().getColor(R.color.black));
            binding.yuzdeText.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            binding.totalAmountText.setTextColor(getResources().getColor(R.color.red));
            binding.profitLossText.setTextColor(getResources().getColor(R.color.red));
            binding.text9.setTextColor(getResources().getColor(R.color.red));
            binding.text10.setTextColor(getResources().getColor(R.color.red));
            binding.text12.setTextColor(getResources().getColor(R.color.red));
            binding.yuzdeText.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void visibleAndEnabledGiris() {
        binding.saveButton.setText("GÜNCELLE");
        binding.piecesText.setEnabled(true);
        binding.buyPriceText.setEnabled(true);
        binding.komisyonText.setEnabled(true);
        binding.dateSellText.setEnabled(true);
        binding.dateBuyText.setEnabled(true);
        binding.stockNameText.setEnabled(true);
        binding.stockNameText.setText("");
        binding.buyPriceText.setText("");
        binding.piecesText.setText("");
        binding.sellPriceText.setText("");
        binding.komisyonText.setText("");
        binding.dateBuyText.setText("");
        binding.dateSellText.setText("");
        binding.totalAmountText.setText("");
        binding.amountText.setText("");
        binding.profitLossText.setText("");
    }

    private void dateSellTextCalendar() {
        binding.dateSellText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        StockDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                binding.dateSellText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void dateBuyTextCalendar() {
        binding.dateBuyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        StockDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                binding.dateBuyText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (binding.stockNameText.getText().toString().isEmpty() ||
                            binding.piecesText.getText().toString().isEmpty() ||
                            binding.buyPriceText.getText().toString().isEmpty()
                    ) {
                        Snackbar.make(v, "Gerekli alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(binding.stockNameText.getText().length()>10)
                    {
                        Snackbar.make(v, "İsim alanı en fazla 10 karakter olmalıdır!", Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        try {
                            calculateAmountAndProfitLoss();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name", binding.stockNameText.getText().toString());
                            contentValues.put("pieces", binding.piecesText.getText().toString());
                            contentValues.put("buyDate", binding.dateBuyText.getText().toString());
                            contentValues.put("sellDate", binding.dateSellText.getText().toString());
                            contentValues.put("stockPriceBuy", binding.buyPriceText.getText().toString());
                            contentValues.put("stockPriceSell", binding.sellPriceText.getText().toString());
                            contentValues.put("amount", binding.amountText.getText().toString());
                            contentValues.put("profitAndLoss", binding.profitLossText.getText().toString());
                            contentValues.put("komisyon", binding.komisyonText.getText().toString());
                            contentValues.put("total", binding.totalAmountText.getText().toString());
                            contentValues.put("yuzde", binding.yuzdeText.getText().toString());

                            database = dbHelper.getWritableDatabase();
                            long l= database.update(TABLENAME,contentValues,"id="+id,null);

                            if (l != -1) {
                                Toast.makeText(StockDetails.this, "Güncellendi", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(StockDetails.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(StockDetails.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            }
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void calculateAmountAndProfitLoss() {
        try {
            double sellPrice;
            double komisyon ;
            int pieces;
            double amount;
            double profitLoss;
            double totalAmount;
            if (!binding.sellPriceText.getText().toString().isEmpty()&&!binding.komisyonText.getText().toString().isEmpty()) {
                sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.piecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = (sellPrice * pieces) - amount - komisyon;
                totalAmount = amount + profitLoss;
                yuzde=(profitLoss/amount)*100;
            }
            else if(!binding.sellPriceText.getText().toString().isEmpty()&&binding.komisyonText.getText().toString().isEmpty()){
                sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
                komisyon = 0;
                buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.piecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = (sellPrice * pieces) - amount - komisyon;
                totalAmount = amount + profitLoss;
                yuzde=(profitLoss/amount)*100;
            }
            else if(binding.sellPriceText.getText().toString().isEmpty()&&!binding.komisyonText.getText().toString().isEmpty()){
                sellPrice = 0;
                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.piecesText.getText().toString());
                amount = buyPrice * pieces;
                profitLoss = amount-komisyon;
                totalAmount = amount-komisyon;
                yuzde=0;
            }
            else{
                sellPrice = 0;
                komisyon = 0;
                buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.piecesText.getText().toString());
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
            if(resultProfitLoss.equals("+")){
                binding.text12.setTextColor(getResources().getColor(R.color.green));
                binding.text10.setTextColor(getResources().getColor(R.color.green));
            } else if (resultProfitLoss.equals("-")) {
                binding.text12.setTextColor(getResources().getColor(R.color.red));
                binding.text10.setTextColor(getResources().getColor(R.color.red));
            }
            else{
                binding.text12.setTextColor(getResources().getColor(R.color.black));
                binding.text10.setTextColor(getResources().getColor(R.color.black));
            }


            if (yuzde > 0) {
                binding.yuzdeText.setTextColor(getResources().getColor(R.color.green));
                binding.text9.setTextColor(getResources().getColor(R.color.green));
            } else if (yuzde < 0) {
                binding.yuzdeText.setTextColor(getResources().getColor(R.color.red));
                binding.text9.setTextColor(getResources().getColor(R.color.red));
            } else {
                binding.yuzdeText.setTextColor(getResources().getColor(R.color.black));
                binding.text9.setTextColor(getResources().getColor(R.color.black));
            }

        }
        catch (Exception e) {
            Toast.makeText(StockDetails.this,"hata",Toast.LENGTH_SHORT).show();
        }

}

    private void editData() {
        if(getIntent().getBundleExtra("userData")!=null){
            Bundle bundle= getIntent().getBundleExtra("userData");
            assert bundle != null;
            binding.stockNameText.setText(bundle.getString("name"));
            binding.piecesText.setText(String.valueOf(bundle.getInt("pieces")) );
            binding.buyPriceText.setText(String.valueOf(bundle.getDouble("stockPriceBuy")) );
            binding.sellPriceText.setText(String.valueOf(bundle.getDouble("stockPriceSell")) );
            binding.komisyonText.setText(String.valueOf(bundle.getDouble("komisyon")) );
            binding.dateBuyText.setText(bundle.getString("buyDate"));
            binding.dateSellText.setText(bundle.getString("sellDate"));
            binding.amountText.setText(String.valueOf(bundle.getDouble("amount")) );
            binding.totalAmountText.setText(String.valueOf(bundle.getDouble("total")) );
            binding.profitLossText.setText(String.valueOf(bundle.getDouble("profitAndLoss")) );
            binding.yuzdeText.setText(String.valueOf(bundle.getDouble("yuzde")) );
            id=bundle.getInt("id");

        }
    }
}