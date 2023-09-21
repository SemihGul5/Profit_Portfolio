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
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.example.profitportfolio.databinding.ActivityStockDetailsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.SplittableRandom;

public class StockDetails extends AppCompatActivity {
    private ActivityStockDetailsBinding binding;
    SQLiteDatabase database;
    DbHelper dbHelper;

    int id;
    public static String color="";
    double pieces,stockPriceBuy,amount,prof,komisyon,total,yuzde,ortMaliyetX,sellPiecesX,kalanAdet,maliyetKomisyon;
    String stockName,buydate,sellDate,stockPriceSell,satisTutari;
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
        buyButton();
        sellButton();

    }
    private void getBundle(Intent intent){
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("name",stockName);
        bundle.putDouble("pieces",pieces);
        bundle.putString("buyDate",buydate);
        bundle.putString("sellDate",sellDate);
        bundle.putDouble("stockPriceBuy",stockPriceBuy);
        bundle.putString("stockPriceSell",stockPriceSell);
        bundle.putDouble("amount",amount);
        bundle.putDouble("profitAndLoss",prof);
        bundle.putDouble("komisyon",komisyon);
        bundle.putDouble("total",total);
        bundle.putDouble("yuzde",yuzde);
        bundle.putDouble("ortMaliyet",ortMaliyetX);
        bundle.putDouble("sellPieces",sellPiecesX);
        bundle.putDouble("kalanAdet",kalanAdet);
        bundle.putString("satisTutari",satisTutari);
        bundle.putDouble("maliyetKomisyon",maliyetKomisyon);
        intent.putExtra("userData", bundle);
    }

    private void buyButton() {
        binding.buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockDetails.this, BuyActivity.class);
                getBundle(intent);
                startActivity(intent);


            }
        });
    }

    private void sellButton() {
        binding.sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StockDetails.this, SellActivity.class);
                getBundle(intent);
                startActivity(intent);
            }
        });

    }

    private void colorControl() {
        double amount,total,prof;
        amount=Double.parseDouble(binding.amountText.getText().toString());
        total=Double.parseDouble(binding.totalAmountText.getText().toString());
        if(total>amount){
            binding.profitLossText.setTextColor(getResources().getColor(R.color.green));
            binding.yuzdeText.setTextColor(getResources().getColor(R.color.green));

        }
        else if(total==amount){
            binding.profitLossText.setTextColor(getResources().getColor(R.color.black));
            binding.yuzdeText.setTextColor(getResources().getColor(R.color.black));
        }
        else{
            binding.profitLossText.setTextColor(getResources().getColor(R.color.red));
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

            stockName=bundle.getString("name");
            binding.stockNameText.setText(stockName);

            pieces=bundle.getDouble("pieces");


            stockPriceBuy=bundle.getDouble("stockPriceBuy");
            double alis = bundle.getDouble("stockPriceBuy");
            String aliss = String.format("%.2fTL", alis);
            binding.buyPriceText.setText(aliss);

            stockPriceSell=bundle.getString("stockPriceSell");

            if(stockPriceSell.equals("")){
                stockPriceSell="null";
                binding.sellPriceText.setText(stockPriceSell);
            }
            else{
                double sell= Double.parseDouble(stockPriceSell);
                String p = String.format("%.2fTL", sell);
                binding.sellPriceText.setText(p);
            }


            komisyon=bundle.getDouble("komisyon");
            double kom = bundle.getDouble("komisyon");
            String komisyons = String.format("%.2fTL", kom);
            binding.komisyonText.setText(komisyons);

            buydate=bundle.getString("buyDate");
            binding.dateBuyText.setText(buydate);

            sellDate=bundle.getString("sellDate");
            binding.dateSellText.setText(sellDate);

            amount=bundle.getDouble("amount");
            String maliyet= String.valueOf(bundle.getDouble("amount"));


            total=bundle.getDouble("total");
            binding.totalAmountText.setText(String.valueOf(total));

            prof=bundle.getDouble("profitAndLoss" );
            String pronls = String.format("%.2fTL", bundle.getDouble("profitAndLoss" ));
            binding.profitLossText.setText(pronls);

            yuzde=bundle.getDouble("yuzde");
            double yuzde = bundle.getDouble("yuzde");
            String formattedYuzde ="%" +String.format("%.2f", yuzde);
            binding.yuzdeText.setText(formattedYuzde);

            sellPiecesX=bundle.getDouble("sellPieces" );
            maliyetKomisyon=bundle.getDouble("maliyetKomisyon" );
            binding.amountText.setText(String.valueOf(maliyetKomisyon));


            ortMaliyetX=bundle.getDouble("ortMaliyet" );
            String ortMaliyet = String.format("%.2f", bundle.getDouble("ortMaliyet" ))+" TL";

            id=bundle.getInt("id");

            kalanAdet=bundle.getDouble("kalanAdet");
            binding.piecesText.setText(String.valueOf(kalanAdet));



            String sTutar=bundle.getString("satisTutari");
            if(sTutar.equals("")){
                satisTutari="null";
                //binding.sellPriceText.setText("null");
            }
            else{
                double satisTutar= Double.parseDouble(sTutar);
                satisTutari= String.valueOf(satisTutar);
                String sp = String.format("%.2f TL", satisTutar);
                //binding.sellPriceText.setText(sp);
            }


        }
    }
}