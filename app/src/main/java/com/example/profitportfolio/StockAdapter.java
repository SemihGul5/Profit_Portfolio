package com.example.profitportfolio;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profitportfolio.databinding.RecyclerListBinding;

import java.util.ArrayList;
import java.util.Objects;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockHolder> {
    ArrayList<Stock> stockArrayList;
    Context context;
    SQLiteDatabase database;


    public StockAdapter(ArrayList<Stock> stockArrayList, Context context, SQLiteDatabase database) {
        this.stockArrayList = stockArrayList;
        this.context = context;
        this.database = database;
    }


    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerListBinding binding = RecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StockHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {



        final Stock stock = stockArrayList.get(position);
        holder.binding.recyclerAmountNameText.setText(String.valueOf(stock.getAmount()));
        holder.binding.recyclerDateText.setText(stock.getBuyDate());
        holder.binding.recyclerStockNameText.setText(stock.getName());
        holder.binding.recyclerDateText.setText(stock.getBuyDate());

        // Veri geldiğinde renklendirme işlemi yap
        if (stockArrayList.size() > 0) {
            holder.binding.recyclerKarZararNameText.setText(String.valueOf(stock.getProfitAndLoss()));

            String color = stock.getColor(stock.getProfitAndLoss());

            if (Objects.equals(color, "red")) {
                holder.binding.recyclerKarZararNameText.setTextColor(context.getColor(R.color.red));
            } else if (Objects.equals(color, "green")) {
                holder.binding.recyclerKarZararNameText.setTextColor(context.getColor(R.color.green));
            }
        } else {
            // Veri yoksa metni temizle veya istediğiniz bir mesajı göster
            holder.binding.recyclerKarZararNameText.setText("");
        }
    }


    @Override
    public int getItemCount() {
        return stockArrayList.size();
    }

    public class StockHolder extends RecyclerView.ViewHolder{
        RecyclerListBinding binding;

        public StockHolder(RecyclerListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

}
