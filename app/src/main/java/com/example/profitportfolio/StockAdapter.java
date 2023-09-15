package com.example.profitportfolio;



import static com.example.profitportfolio.DbHelper.TABLENAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.profitportfolio.databinding.RecyclerListBinding;

import java.util.ArrayList;
import java.util.Objects;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockHolder> {
    ArrayList<Stock> stockArrayList;
    Context context;
    SQLiteDatabase database;
    int singleData;


    public StockAdapter(ArrayList<Stock> stockArrayList, Context context, SQLiteDatabase database,int singleData) {
        this.stockArrayList = stockArrayList;
        this.context = context;
        this.database = database;
        this.singleData=singleData;
    }


    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerListBinding binding = RecyclerListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StockHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, @SuppressLint("RecyclerView") int position) {



        final Stock stock = stockArrayList.get(position);
        holder.binding.recyclerAmountNameText.setText(String.valueOf(stock.getTotalAmount()));
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


        holder.binding.recyclerFlowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 3 noktalı menüyü bağlama işlemi
                PopupMenu popupMenu= new PopupMenu(context,holder.binding.recyclerFlowMenu);
                popupMenu.inflate(R.menu.flow_menu);


                //3 noktalı menü elemanlarına tıklanınca ne olacağının yazıldığı bölüm
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        try {
                            if(menuItem.getItemId()==R.id.flowDelete){

                                DbHelper dbHelper=new DbHelper(context);
                                database=dbHelper.getReadableDatabase();
                                long l;
                                l= database.delete(TABLENAME,"id="+stock.getId(),null);
                                if(l!=-1){
                                    Toast.makeText(context,"Silindi",Toast.LENGTH_SHORT).show();
                                    stockArrayList.remove(position);
                                    notifyDataSetChanged();
                                    ((MainActivity) context).getTotalPortfolio();
                                    ((MainActivity) context).getProfitLoss();


                                }
                                else{
                                    Toast.makeText(context,"İşlem başarısız",Toast.LENGTH_SHORT).show();
                                }
                            }

                            else if (menuItem.getItemId()==R.id.flowEdit) {

                                Bundle bundle=new Bundle();
                                bundle.putInt("id",stock.getId());
                                bundle.putString("name",stock.getName());
                                bundle.putInt("pieces",stock.getPieces());
                                bundle.putString("buyDate",stock.getBuyDate());
                                bundle.putString("sellDate",stock.getSellDate());
                                bundle.putDouble("stockPriceBuy",stock.getStockPriceBuy());
                                bundle.putDouble("stockPriceSell",stock.getStockPriceSell());
                                bundle.putDouble("amount",stock.getAmount());
                                bundle.putDouble("profitAndLoss",stock.getProfitAndLoss());
                                bundle.putDouble("komisyon",stock.getKomisyon());
                                bundle.putDouble("total",stock.getTotalAmount());

                                Intent intent=new Intent(context, StockDetails.class);
                                intent.putExtra("userData",bundle);
                                context.startActivity(intent);
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Toast.makeText(context,"hata",Toast.LENGTH_SHORT).show();
                            Log.e("FlowMenuError", "Flow menü hatası: " + e.getMessage());
                        }



                        return false;
                    }
                });
                popupMenu.show();


            }
        });




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
