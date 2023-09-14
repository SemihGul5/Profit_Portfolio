package com.example.profitportfolio;

import static com.example.profitportfolio.AddStock.greenColor;
import static com.example.profitportfolio.AddStock.redColor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profitportfolio.databinding.RecyclerListBinding;

import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockHolder> {
    ArrayList<Stock> stockArrayList;
    Context context;
    SQLiteDatabase database;

    public StockAdapter(ArrayList<Stock> stockArrayList, Context context, SQLiteDatabase database) {
        this.stockArrayList = stockArrayList;
        this.context = context;
        this.database = database;
    }


    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerListBinding binding = RecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StockHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        final Stock stock = stockArrayList.get(position);
        holder.binding.recyclerAmountNameText.setText((int) stock.getAmount());
        holder.binding.recyclerDateText.setText(stock.getBuyDate());

        double profitLoss= stock.getProfitAndLoss();
        if(profitLoss<0){
            holder.binding.recyclerKarZararNameText.setText((int) stock.getProfitAndLoss());
            holder.binding.recyclerKarZararNameText.setTextColor(redColor);
        }
        else if (profitLoss>0){
            holder.binding.recyclerKarZararNameText.setText((int) stock.getProfitAndLoss());
            holder.binding.recyclerKarZararNameText.setTextColor(greenColor);
        }
        else{
            holder.binding.recyclerKarZararNameText.setText((int) stock.getProfitAndLoss());
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
