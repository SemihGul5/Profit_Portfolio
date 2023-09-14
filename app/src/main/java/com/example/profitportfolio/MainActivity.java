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

        adapter=new StockAdapter(stockArrayList,this,database);
        binding.recyclerView.setAdapter(adapter);
        
        getData();
        fab();

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

            Stock stock= new Stock(id,name,pieces,dateBuy,dateSell,stockPricesBuy,stockPricesSell,amount,profitLoss);
            stockArrayList.add(stock);
        }
        cursor.close();
        adapter.notifyDataSetChanged();







    }


}