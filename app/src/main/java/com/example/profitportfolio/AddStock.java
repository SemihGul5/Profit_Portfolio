package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityAddStockBinding;
import com.google.android.material.snackbar.Snackbar;

public class AddStock extends AppCompatActivity {
    private ActivityAddStockBinding binding;
    DbHelper dbHelper;
    SQLiteDatabase database;

    double buyPrice;
    int pieces;
    double sellPrice;
    double amount;
    double profitLoss;
    double komisyon;
    public static String color="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle("Hisse Ekle");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        dbHelper = new DbHelper(AddStock.this);
        addTextWatchers();
        saveData();
    }

    private void saveData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.stockNameText.getText().toString().isEmpty() ||
                        binding.piecesText.getText().toString().isEmpty() ||
                        binding.buyPriceText.getText().toString().isEmpty() ||
                        binding.sellPriceText.getText().toString().isEmpty() ||
                        binding.komisyonText.getText().toString().isEmpty()) {
                    Snackbar.make(v, "Girilmesi zorunlu bilgiler eksiksiz girilmelidir.", Snackbar.LENGTH_SHORT).show();
                }
                else if(binding.stockNameText.getText().length()>10)
                    {
                        Snackbar.make(v, "İsim alanı en fazla 10 karakter olmalıdır!", Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        try {
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

                            database = dbHelper.getWritableDatabase();
                            long result = database.insert(TABLENAME, null, contentValues);

                            if (result != -1) {
                                Toast.makeText(AddStock.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();

                                Intent intent= new Intent(AddStock.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(AddStock.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AddStock.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });
    }
    private void addTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Değişmeden önceki durum

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Text değiştiğinde yapılacak işlemler
                calculateAmountAndProfitLoss();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Text değiştikten sonra durum
            }
        };

        binding.buyPriceText.addTextChangedListener(textWatcher);
        binding.piecesText.addTextChangedListener(textWatcher);
        binding.sellPriceText.addTextChangedListener(textWatcher);
        binding.komisyonText.addTextChangedListener(textWatcher);
    }

    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    private void calculateAmountAndProfitLoss() {
        try {


            buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
            pieces = Integer.parseInt(binding.piecesText.getText().toString());
            sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());
            komisyon = Double.parseDouble(binding.komisyonText.getText().toString());

            amount = buyPrice * pieces;
            profitLoss = (sellPrice * pieces) - amount - komisyon;


            String resultProfitLoss = "";
            if (profitLoss < 0) {
                resultProfitLoss = "-";
                color="red";
            } else if (profitLoss > 0) {
                resultProfitLoss = "+";
                color="green";
            } else {
                resultProfitLoss = "";
            }

            if (resultProfitLoss.equals("+")) {
                binding.amountText.setText(String.format("%.2f", amount));
                binding.profitLossText.setText(String.format("%.2f", profitLoss));
                binding.profitLossText.setTextColor(getResources().getColor(R.color.green));
            } else if (resultProfitLoss.equals("-")) {
                binding.amountText.setText(String.format("%.2f", amount));
                binding.profitLossText.setText(String.format("%.2f", profitLoss));
                binding.profitLossText.setTextColor(getResources().getColor(R.color.red));
            } else {
                binding.amountText.setText(String.format("%.2f", amount));
                binding.profitLossText.setText(String.format("%.2f", profitLoss));
            }

        } catch (NumberFormatException e) {
            binding.amountText.setText("");
            binding.profitLossText.setText("");
        }
    }

}
