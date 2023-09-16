package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Stock> stockArrayList;
    SQLiteDatabase database;
    DbHelper dbHelper;
    StockAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        dbHelper= new DbHelper(this);

        stockArrayList=new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));

        adapter=new StockAdapter(stockArrayList,MainActivity.this,database,R.layout.recycler_list);
        getData();
        binding.recyclerView.setAdapter(adapter);
        

        fab();
        getTotalPortfolio();
        getProfitLoss();
        getYuzde();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getTotalPortfolio() {
        double totalPortfolio = 0;
        database = dbHelper.getReadableDatabase();
        // Bütün hisselerin toplam adetini al
        Cursor cursor = database.rawQuery("SELECT SUM(pieces) FROM " + DbHelper.TABLENAME, null);
        int totalPieces = 0;

        if (cursor.moveToFirst()) {
            totalPieces = cursor.getInt(0);
        }
        cursor.close();

        // Alış fiyatlarının ortalamasını al
        cursor = database.rawQuery("SELECT AVG(stockPriceBuy) FROM " + DbHelper.TABLENAME, null);
        double avgBuyPrice = 0;

        if (cursor.moveToFirst()) {
            avgBuyPrice = cursor.getDouble(0);
        }

        cursor.close();

        // Maliyeti hesapla
        double totalCost = totalPieces * avgBuyPrice;
        double sumPort=totalCost+getProfitLoss();

        binding.toplamDegerText.setText(" " + String.valueOf(totalCost) + " TL");
        binding.toplamText.setText(" "+String.valueOf(sumPort)+" TL");
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getYuzde() {
        double totalPortfolio = 0;
        database = dbHelper.getReadableDatabase();

        // SQL sorgusu ile toplam kar/zararı ve toplam tutarı al.
        Cursor cursor = database.rawQuery("SELECT SUM(profitAndLoss), SUM(amount) FROM " + DbHelper.TABLENAME, null);

        if (cursor.moveToFirst()) {
            double totalProfitLoss = cursor.getDouble(0);
            double totalAmount = cursor.getDouble(1);

            if (totalAmount != 0) {
                // Yüzdeyi hesapla ve ekrana yazdır.
                double yuzde = (totalProfitLoss / totalAmount) * 100;
                if(yuzde<0)
                {
                    binding.getiriText.setTextColor(getResources().getColor(R.color.red));
                }
                else if(yuzde>0){
                    binding.getiriText.setTextColor(getResources().getColor(R.color.green));
                }
                else{
                    binding.getiriText.setTextColor(getResources().getColor(R.color.black));
                }
                binding.getiriText.setText("% " + String.format("%.2f", yuzde));

            } else {
                binding.getiriText.setText("% 0.00");
            }
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }





    @SuppressLint("NotifyDataSetChanged")
    public double getProfitLoss() {
        double totalProfitLoss = 0;
        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT SUM(profitAndLoss) FROM " + TABLENAME, null);

        if (cursor.moveToFirst()) {
            totalProfitLoss = cursor.getDouble(0);
        }

        cursor.close();
        String profitLossText = " " + String.valueOf(totalProfitLoss) + " TL";

        if (totalProfitLoss > 0.0) {
            binding.karZararText.setTextColor(getResources().getColor(R.color.green));
        } else if (totalProfitLoss < 0.0) {
            binding.karZararText.setTextColor(getResources().getColor(R.color.red));
        } else {
            binding.karZararText.setTextColor(getResources().getColor(android.R.color.black));
        }

        binding.karZararText.setText(profitLossText);
        adapter.notifyDataSetChanged();
        return totalProfitLoss;
    }


    private void fab() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, AddStock.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getData() {
        stockArrayList.clear();
        database=dbHelper.getReadableDatabase();
        Cursor cursor= database.rawQuery("Select * from "+TABLENAME+"",null);

        int idIx=cursor.getColumnIndex("id");
        int nameIx=cursor.getColumnIndex("name");
        int piecesIx=cursor.getColumnIndex("pieces");
        int dateBuyIx=cursor.getColumnIndex("buyDate");
        int dateSellIx=cursor.getColumnIndex("sellDate");
        int stockPricesBuyIx=cursor.getColumnIndex("stockPriceBuy");
        int stockPricesSellIx=cursor.getColumnIndex("stockPriceSell");
        int amountIx=cursor.getColumnIndex("amount");
        int profitLossIx=cursor.getColumnIndex("profitAndLoss");
        int komisyonIx=cursor.getColumnIndex("komisyon");
        int totalAmountIx=cursor.getColumnIndex("total");
        int yuzdeIx=cursor.getColumnIndex("yuzde");

        while (cursor.moveToNext())
        {
            int id=cursor.getInt(idIx);
            String name=cursor.getString(nameIx);
            int pieces=cursor.getInt(piecesIx);
            String dateBuy=cursor.getString(dateBuyIx);
            String dateSell=cursor.getString(dateSellIx);
            double stockPricesBuy=cursor.getDouble(stockPricesBuyIx);
            double stockPricesSell=cursor.getDouble(stockPricesSellIx);
            double amount=cursor.getDouble(amountIx);
            double profitLoss=cursor.getDouble(profitLossIx);
            double komisyon=cursor.getDouble(komisyonIx);
            double total=cursor.getDouble(totalAmountIx);
            double yuzde=cursor.getDouble(yuzdeIx);


            Stock stock= new Stock(id,name,pieces,dateBuy,dateSell,stockPricesBuy,stockPricesSell,amount,profitLoss,komisyon,total,yuzde);
            stockArrayList.add(stock);
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        stockArrayList.clear(); // Önceki verileri temizle
        getData(); // Verileri yeniden yükle
        adapter.notifyDataSetChanged(); // Adapter'a veri değişikliği bildir
    }
}