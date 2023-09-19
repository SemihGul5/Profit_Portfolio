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
    public static String color="";
    double ortMaliyet,sellPieces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStockDetailsBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        setTitle("Hisse Bilgileri");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        visibleAndEnabledGiris();


        dbHelper= new DbHelper(this);

        editData();
        colorControl();

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


    @SuppressLint("DefaultLocale")
    private void editData() {
        if(getIntent().getBundleExtra("userData")!=null){
            Bundle bundle= getIntent().getBundleExtra("userData");
            assert bundle != null;
            binding.stockNameText.setText(bundle.getString("name"));
            binding.piecesText.setText(String.valueOf(bundle.getInt("pieces")) );
            binding.buyPriceText.setText(String.valueOf(bundle.getDouble("stockPriceBuy")) );
            double sellp = bundle.getDouble("stockPriceSell");
            String sellsp = String.format("%.2f", sellp);
            binding.sellPriceText.setText(sellsp);
            binding.komisyonText.setText(String.valueOf(bundle.getDouble("komisyon")) );
            binding.dateBuyText.setText(bundle.getString("buyDate"));
            binding.dateSellText.setText(bundle.getString("sellDate"));
            binding.amountText.setText(String.valueOf(bundle.getDouble("amount")) );
            binding.totalAmountText.setText(String.valueOf(bundle.getDouble("total")) );
            binding.profitLossText.setText(String.valueOf(bundle.getDouble("profitAndLoss")) );
            double yuzde = bundle.getDouble("yuzde");
            String formattedYuzde = String.format("%.2f", yuzde);
            binding.yuzdeText.setText(formattedYuzde);
           sellPieces=bundle.getDouble("sellPieces" );
            binding.ortMaliyetText.setText(String.valueOf(bundle.getDouble("ortMaliyet" )));
            id=bundle.getInt("id");

        }
    }
}