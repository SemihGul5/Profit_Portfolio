package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Stock> stockArrayList;
    SQLiteDatabase database;
    DbHelper dbHelper;
    StockAdapter adapter;


    @SuppressLint("NotifyDataSetChanged")
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
        //getTotalPortfolio();
        calculatePortfolioValue();
        getProfitLoss();
        getYuzde();
        adapter.notifyDataSetChanged();

        binding.searchText.setVisibility(View.GONE);
        binding.textInputLayout6.setVisibility(View.GONE);


        searching();


    }

    private void searching() {
        // TextWatcher eklemek için örnek kod
        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Metin değişmeden önce yapılacak işlemler
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Metin her değiştiğinde yapılacak işlemler
                String searchText = s.toString().toUpperCase();
                // Veritabanında bu metni kullanarak arama yapın ve sonuçları görünüme yükleyin
                performSearch(searchText);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Metin değiştikten sonra yapılacak işlemler
            }
        });

    }
    // Arama işlevselliğini gerçekleştiren metot
    private void performSearch(String searchText) {
        if (searchText.isEmpty()) {
            // Eğer arama metni boşsa, tüm verileri göster
            getData();
        } else {
            stockArrayList.clear();
            // Veritabanında name sütununda arama yap
            ArrayList<Stock> searchResults = searchByName(searchText);
            // Arama sonuçlarını RecyclerView'a yükleyin
            adapter.setData(searchResults);
        }
    }


    // Veritabanında name sütununda arama yapma metodu
    @SuppressLint("NotifyDataSetChanged")
    private ArrayList<Stock> searchByName(String name) {
        ArrayList<Stock> results = new ArrayList<>();
        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLENAME + " WHERE name LIKE ?", new String[]{"%" + name + "%"});

        int idIx = cursor.getColumnIndex("id");
        int nameIx = cursor.getColumnIndex("name");
        int piecesIx = cursor.getColumnIndex("pieces");
        int dateBuyIx = cursor.getColumnIndex("buyDate");
        int dateSellIx = cursor.getColumnIndex("sellDate");
        int stockPricesBuyIx = cursor.getColumnIndex("stockPriceBuy");
        int stockPricesSellIx = cursor.getColumnIndex("stockPriceSell");
        int amountIx = cursor.getColumnIndex("amount");
        int profitLossIx = cursor.getColumnIndex("profitAndLoss");
        int komisyonIx = cursor.getColumnIndex("komisyon");
        int totalAmountIx = cursor.getColumnIndex("total");
        int yuzdeIx = cursor.getColumnIndex("yuzde");
        int ortIx = cursor.getColumnIndex("ortMaliyet");
        int sellPIx = cursor.getColumnIndex("sellPieces");
        int kalanAdetIx = cursor.getColumnIndex("kalanAdet");
        int satisIx = cursor.getColumnIndex("satisTutari");
        int mkix = cursor.getColumnIndex("maliyetKomisyon");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIx);
            String stockName = cursor.getString(nameIx);
            double pieces = cursor.getDouble(piecesIx);
            String buyDate = cursor.getString(dateBuyIx);
            String sellDate = cursor.getString(dateSellIx);
            double stockPriceBuy = cursor.getDouble(stockPricesBuyIx);
            String stockPriceSell = cursor.getString(stockPricesSellIx);
            double amount = cursor.getDouble(amountIx);
            double profitLoss = cursor.getDouble(profitLossIx);
            double komisyon = cursor.getDouble(komisyonIx);
            double total = cursor.getDouble(totalAmountIx);
            double yuzde = cursor.getDouble(yuzdeIx);
            double ortMaliyet = cursor.getDouble(ortIx);
            double sellPieces = cursor.getDouble(sellPIx);
            double kalanAdet = cursor.getDouble(kalanAdetIx);
            String satisTutari = cursor.getString(satisIx);
            double maliyetKomisyon = cursor.getDouble(mkix);

            Stock stock = new Stock(id, stockName, pieces, buyDate, sellDate, stockPriceBuy, stockPriceSell, amount, profitLoss, komisyon, total, yuzde, ortMaliyet, sellPieces, kalanAdet, satisTutari, maliyetKomisyon);
            results.add(stock);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        return results;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_flow_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.sortStock){
            bottomDialogShow();
        }
        else if (item.getItemId() == R.id.searchStock) {
            if (binding.searchText.getVisibility() == View.VISIBLE) {
                // Geri tuşuna basıldığında, searchable alanları gizle
                binding.searchText.setVisibility(View.GONE);
                binding.textInputLayout6.setVisibility(View.GONE);
            } else {
                // "Ara" öğesine tıklandığında, searchable alanları görünür yap
                binding.searchText.setVisibility(View.VISIBLE);
                binding.textInputLayout6.setVisibility(View.VISIBLE);

                // ActionBar'ın geri butonunu özelleştir
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayHomeAsUpEnabled(true); // Geri butonunu göster
                    actionBar.setDisplayShowHomeEnabled(true); // Geri butonunu göster
                    actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24); // Geri butonunun ikonunu ayarla
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void dbCursor(String query){
        ArrayList<Stock> results = new ArrayList<>();
        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        int idIx = cursor.getColumnIndex("id");
        int nameIx = cursor.getColumnIndex("name");
        int piecesIx = cursor.getColumnIndex("pieces");
        int dateBuyIx = cursor.getColumnIndex("buyDate");
        int dateSellIx = cursor.getColumnIndex("sellDate");
        int stockPricesBuyIx = cursor.getColumnIndex("stockPriceBuy");
        int stockPricesSellIx = cursor.getColumnIndex("stockPriceSell");
        int amountIx = cursor.getColumnIndex("amount");
        int profitLossIx = cursor.getColumnIndex("profitAndLoss");
        int komisyonIx = cursor.getColumnIndex("komisyon");
        int totalAmountIx = cursor.getColumnIndex("total");
        int yuzdeIx = cursor.getColumnIndex("yuzde");
        int ortIx = cursor.getColumnIndex("ortMaliyet");
        int sellPIx = cursor.getColumnIndex("sellPieces");
        int kalanAdetIx = cursor.getColumnIndex("kalanAdet");
        int satisIx = cursor.getColumnIndex("satisTutari");
        int mkix = cursor.getColumnIndex("maliyetKomisyon");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idIx);
            String stockName = cursor.getString(nameIx);
            double pieces = cursor.getDouble(piecesIx);
            String buyDate = cursor.getString(dateBuyIx);
            String sellDate = cursor.getString(dateSellIx);
            double stockPriceBuy = cursor.getDouble(stockPricesBuyIx);
            String stockPriceSell = cursor.getString(stockPricesSellIx);
            double amount = cursor.getDouble(amountIx);
            double profitLoss = cursor.getDouble(profitLossIx);
            double komisyon = cursor.getDouble(komisyonIx);
            double total = cursor.getDouble(totalAmountIx);
            double yuzde = cursor.getDouble(yuzdeIx);
            double ortMaliyet = cursor.getDouble(ortIx);
            double sellPieces = cursor.getDouble(sellPIx);
            double kalanAdet = cursor.getDouble(kalanAdetIx);
            String satisTutari = cursor.getString(satisIx);
            double maliyetKomisyon = cursor.getDouble(mkix);

            Stock stock = new Stock(id, stockName, pieces, buyDate, sellDate, stockPriceBuy, stockPriceSell, amount, profitLoss, komisyon, total, yuzde, ortMaliyet, sellPieces, kalanAdet, satisTutari, maliyetKomisyon);
            results.add(stock);
        }
        cursor.close();
        adapter.setData(results);
        adapter.notifyDataSetChanged();
    }

    private void bottomDialogShow() {
        Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetdialog);

        TextView azText=dialog.findViewById(R.id.azText);
        TextView zaText=dialog.findViewById(R.id.zaText);
        TextView biggerText=dialog.findViewById(R.id.biggerText);
        TextView smallerText=dialog.findViewById(R.id.smallerText);

        azText.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dbCursor("SELECT * FROM " + TABLENAME + " ORDER BY name ASC");
            }
        });
        zaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dbCursor("SELECT * FROM " + TABLENAME + " ORDER BY name DESC");
            }
        });
        biggerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dbCursor("SELECT * FROM " + TABLENAME + " ORDER BY profitAndLoss DESC");

            }
        });
        smallerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dbCursor("SELECT * FROM " + TABLENAME + " ORDER BY profitAndLoss  ASC");

            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public boolean onSupportNavigateUp() {
        // ActionBar'ın geri butonuna basıldığında yapılacak işlem
        binding.searchText.setVisibility(View.GONE);
        binding.textInputLayout6.setVisibility(View.GONE);

        // ActionBar'daki geri butonunu gizle
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void getYuzde() {
        double totalPortfolio = 0;
        database = dbHelper.getReadableDatabase();

        // SQL sorgusu ile toplam kar/zararı ve toplam tutarı al.
        Cursor cursor = database.rawQuery("SELECT SUM(profitAndLoss), SUM(maliyetKomisyon) FROM " + DbHelper.TABLENAME, null);

        if (cursor.moveToFirst()) {
            double totalProfitLoss = cursor.getDouble(0);
            double totalAmount = cursor.getDouble(1);

            if (totalAmount != 0) {
                // Yüzdeyi hesapla ve ekrana yazdır.//
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
                binding.getiriText.setText("%" + String.format("%.2f", yuzde));

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
        String profitLossText = String.format("%.2f",totalProfitLoss) + " TL";

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
        int ortIx=cursor.getColumnIndex("ortMaliyet");
        int sellPIx=cursor.getColumnIndex("sellPieces");
        int kalanAdetIx=cursor.getColumnIndex("kalanAdet");
        int satisIx=cursor.getColumnIndex("satisTutari");
        int mkix=cursor.getColumnIndex("maliyetKomisyon");
        while (cursor.moveToNext())
        {
            int id=cursor.getInt(idIx);
            String name=cursor.getString(nameIx);
            double pieces=cursor.getDouble(piecesIx);
            String dateBuy=cursor.getString(dateBuyIx);
            String dateSell=cursor.getString(dateSellIx);
            double stockPricesBuy=cursor.getDouble(stockPricesBuyIx);
            String stockPricesSell=cursor.getString(stockPricesSellIx);
            double amount=cursor.getDouble(amountIx);
            double profitLoss=cursor.getDouble(profitLossIx);
            double komisyon=cursor.getDouble(komisyonIx);
            double total=cursor.getDouble(totalAmountIx);
            double yuzde=cursor.getDouble(yuzdeIx);
            double ort=cursor.getDouble(ortIx);
            double sellPieces=cursor.getDouble(sellPIx);
            double kalanAdet=cursor.getDouble(kalanAdetIx);
            String satisTutari=cursor.getString(satisIx);
            double maliyetKomisyon=cursor.getDouble(mkix);


            Stock stock= new Stock(id,name,pieces,dateBuy,dateSell,stockPricesBuy,stockPricesSell,amount,profitLoss,komisyon,total,yuzde,ort,sellPieces,kalanAdet,satisTutari,maliyetKomisyon);
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

    @SuppressLint("NotifyDataSetChanged")
    public void calculatePortfolioValue() {
        double maliyetFarki=0;

        database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT  stockPriceSell,stockPriceBuy FROM " + TABLENAME, null);

        if (cursor.moveToFirst()) {
            do {
                double satisFiyati= cursor.getDouble(0);
                double alisFiyati=cursor.getDouble(1);
                if(satisFiyati==0){
                    continue;
                }
                else{
                    maliyetFarki +=(satisFiyati-alisFiyati);
                }


            } while (cursor.moveToNext());
        }

        cursor.close();
        binding.toplamText.setText(String.format("%.2f TL", maliyetFarki));
        if(maliyetFarki>0){
            binding.toplamText.setTextColor(getResources().getColor(R.color.green));
        }
        else if(maliyetFarki<0) {
            binding.toplamText.setTextColor(getResources().getColor(R.color.red));
        }
        else{
            binding.toplamText.setTextColor(getResources().getColor(R.color.black));
        }
        adapter.notifyDataSetChanged();
    }


}