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
    public static int greenColor;
    public static int redColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStockBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle("Hisse Ekle");
        ActionBar actionBar= getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);


        dbHelper= new DbHelper(AddStock.this);
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
                        binding.sellPriceText.getText().toString().isEmpty()) {
                    Snackbar.make(v, "Bilgiler eksiksiz girilmelidir", Snackbar.LENGTH_SHORT).show();
                } else {
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

                        database = dbHelper.getWritableDatabase();
                        long result = database.insert(TABLENAME, null, contentValues);

                        if (result != -1) {
                            Toast.makeText(AddStock.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddStock.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(AddStock.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(AddStock.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    } finally {
                        if (database != null && database.isOpen()) {
                            database.close();
                        }
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
    }

    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    private void calculateAmountAndProfitLoss() {
        try {
            double buyPrice = Double.parseDouble(binding.buyPriceText.getText().toString());
            int pieces = Integer.parseInt(binding.piecesText.getText().toString());
            double sellPrice = Double.parseDouble(binding.sellPriceText.getText().toString());

            double amount = buyPrice * pieces;
            double profitLoss = (sellPrice * pieces) - amount;

            greenColor = getResources().getColor(R.color.green);
            redColor = getResources().getColor(R.color.red);

            String resultProfitLoos="";
            if(profitLoss<0){
                resultProfitLoos="-";
            }
            else if (profitLoss>0) {
                resultProfitLoos="+";
            }
            else {
                resultProfitLoos="";
            }

            if(resultProfitLoos.equals("+")){
                binding.amountText.setText(String.format("%.2f",amount));
                binding.profitLossText.setText(String.format("%.2f",profitLoss));
                binding.profitLossText.setTextColor(greenColor);
            }
            else if(resultProfitLoos.equals("-")){
                binding.amountText.setText(String.format("%.2f",amount));
                binding.profitLossText.setText(String.format("%.2f", profitLoss));
                binding.profitLossText.setTextColor(redColor);
            }
            else{
                binding.amountText.setText(String.format("%.2f",amount));
                binding.profitLossText.setText(String.format("%.2f",profitLoss));
            }

        } catch (NumberFormatException e) {
            binding.amountText.setText("");
            binding.profitLossText.setText("");
        }
    }
}
