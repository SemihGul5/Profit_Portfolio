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
import android.text.BidiFormatter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityAddStockBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class AddStock extends AppCompatActivity {
    private ActivityAddStockBinding binding;
    DbHelper dbHelper;
    SQLiteDatabase database;

    double buyPrice;
    double pieces;
    double sellPrice;
    double amount;
    double profitLoss;
    double komisyon;
    double totalAmount;
    public static String color="";
    double yuzde;
    double ortMaliyet;
    double sellPieces,karZarar,kalanAdetDb,maliyetKomisyon;
    String satisTutari,stockPriceSell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        double k=0;
        binding.komisyonText.setText("Komisyon Tutarı: "+ k);

        setTitle("Hisse Ekle");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        visibleAndEnabledGiris();

        dbHelper = new DbHelper(AddStock.this);

        saveData();

        dateBuyTextCalendar();
        dateSellTextCalendar();



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
                        AddStock.this,
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
                        AddStock.this,
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

    private void visibleAndEnabledGiris(){

        binding.saveButton.setText("KAYDET");
        binding.piecesText.setEnabled(true);
        binding.buyPriceText.setEnabled(true);
        binding.sellPriceText.setEnabled(true);
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
        binding.text9.setText("");
        binding.text10.setText("");
        binding.text11.setText("");
        binding.text12.setText("");
        binding.yuzdeText.setText("");
    }

    private void saveData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(binding.saveButton.getText().equals("KAYDET"))
                {
                    if (binding.stockNameText.getText().toString().isEmpty() ||
                            binding.piecesText.getText().toString().isEmpty() ||
                            binding.buyPriceText.getText().toString().isEmpty() ||
                            binding.komisyonText.getText().toString().isEmpty()
                    ) {
                        Snackbar.make(v, "Gerekli alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                    }
                    else if(binding.stockNameText.getText().length()>5)
                    {
                        Snackbar.make(v, "İsim alanı en fazla 5 karakter olmalıdır!", Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        try {
                            calculateAmountAndProfitLoss();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name", binding.stockNameText.getText().toString());
                            contentValues.put("pieces",pieces);
                            contentValues.put("buyDate", binding.dateBuyText.getText().toString());
                            contentValues.put("sellDate", binding.dateSellText.getText().toString());
                            contentValues.put("stockPriceBuy", binding.buyPriceText.getText().toString());
                            contentValues.put("stockPriceSell", stockPriceSell);
                            contentValues.put("amount", binding.amountText.getText().toString());
                            contentValues.put("profitAndLoss", karZarar);
                            contentValues.put("komisyon", komisyon);
                            contentValues.put("total", binding.totalAmountText.getText().toString());
                            contentValues.put("yuzde", yuzde);
                            contentValues.put("ortMaliyet",ortMaliyet);
                            contentValues.put("sellPieces",sellPieces);
                            contentValues.put("kalanAdet",kalanAdetDb);
                            contentValues.put("satisTutari",satisTutari);
                            contentValues.put("maliyetKomisyon",maliyetKomisyon);
                            database = dbHelper.getWritableDatabase();

                            long result = database.insert(TABLENAME, null, contentValues);

                            if (result != -1) {
                                Toast.makeText(AddStock.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();

                                Intent intent= new Intent(AddStock.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                /*binding.textView3.setVisibility(View.VISIBLE);
                                binding.textView5.setVisibility(View.VISIBLE);
                                binding.textView6.setVisibility(View.VISIBLE);

                                binding.saveButton.setText("Yeni Hisse Girişi");
                                binding.piecesText.setEnabled(false);
                                binding.buyPriceText.setEnabled(false);
                                binding.komisyonText.setEnabled(false);
                                binding.sellPriceText.setEnabled(false);
                                binding.stockNameText.setEnabled(false);
                                binding.dateSellText.setEnabled(false);
                                binding.dateBuyText.setEnabled(false);*/

                            } else {
                                Toast.makeText(AddStock.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AddStock.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                else{
                    visibleAndEnabledGiris();
                }
            }
        });
    }


    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    private void calculateAmountAndProfitLoss() {
        try {
            if (!binding.sellPriceText.getText().toString().isEmpty()&&!binding.komisyonText.getText().toString().isEmpty()) {
                sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                pieces = Double.parseDouble(binding.piecesText.getText().toString());
                amount = (buyPrice * pieces);
                profitLoss = (sellPrice * pieces) - amount-komisyon;
                totalAmount = amount + profitLoss;
                yuzde=(profitLoss/(amount+komisyon))*100;
                ortMaliyet=amount/pieces;
                sellPieces=pieces;
                kalanAdetDb=0;
                satisTutari= String.valueOf(totalAmount);
                stockPriceSell=binding.sellPriceText.getText().toString();
            }
            else if(!binding.sellPriceText.getText().toString().isEmpty()&&binding.komisyonText.getText().toString().isEmpty()){
                sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
                komisyon = 0;
                 buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                 pieces = Integer.parseInt(binding.piecesText.getText().toString());
                 amount = (buyPrice * pieces);
                 profitLoss = (sellPrice * pieces) - amount-komisyon;
                 totalAmount = amount + profitLoss;
                yuzde=(profitLoss/(amount+komisyon))*100;
                ortMaliyet=amount/pieces;
                kalanAdetDb=0;
                sellPieces=pieces;
                satisTutari= String.valueOf(totalAmount);
                stockPriceSell=binding.sellPriceText.getText().toString();
            }
            else if(binding.sellPriceText.getText().toString().isEmpty()&&!binding.komisyonText.getText().toString().isEmpty()){
                sellPrice = 0;
                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                 buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
                 pieces = Integer.parseInt(binding.piecesText.getText().toString());
                 amount = (buyPrice * pieces);
                 profitLoss = -komisyon;
                 totalAmount = amount;
                 yuzde=(profitLoss/(amount+komisyon))*100;
                 ortMaliyet=amount/pieces;
                 sellPieces=0;
                 kalanAdetDb=pieces;
                 satisTutari="";
                stockPriceSell="";
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
                ortMaliyet=amount/pieces;
                sellPieces=0;
                kalanAdetDb=pieces;
                satisTutari="";
                stockPriceSell="";
            }
            karZarar=totalAmount-amount-komisyon;
            maliyetKomisyon=amount+komisyon;
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
            binding.profitLossText.setText(String.format("%.3f", profitLoss));
            binding.totalAmountText.setText(String.format("%.2f", totalAmount));
            binding.yuzdeText.setText(String.format("%.2f", yuzde));
            binding.text9.setText("%");
            binding.text10.setText("TL");
            binding.text11.setText("TL");
            binding.text12.setText("TL");
            if(resultProfitLoss.equals("+")){
                binding.text12.setTextColor(getResources().getColor(R.color.green));
                binding.text10.setTextColor(getResources().getColor(R.color.green));
                binding.profitLossText.setTextColor(getResources().getColor(R.color.green));
                binding.totalAmountText.setTextColor(getResources().getColor(R.color.green));



            } else if (resultProfitLoss.equals("-")) {
                binding.text12.setTextColor(getResources().getColor(R.color.red));
                binding.text10.setTextColor(getResources().getColor(R.color.red));
                binding.profitLossText.setTextColor(getResources().getColor(R.color.red));
                binding.totalAmountText.setTextColor(getResources().getColor(R.color.red));
            }
            else{
                binding.text12.setTextColor(getResources().getColor(R.color.black));
                binding.text10.setTextColor(getResources().getColor(R.color.black));
                binding.profitLossText.setTextColor(getResources().getColor(R.color.black));
                binding.totalAmountText.setTextColor(getResources().getColor(R.color.black));
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
            Toast.makeText(AddStock.this,"hata",Toast.LENGTH_SHORT).show();
        }
    }

}
