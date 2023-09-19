package com.example.profitportfolio;



import static com.example.profitportfolio.DbHelper.TABLENAME;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
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
        String amount=String.format("%.2f",stock.getTotalAmount())+" TL";
        holder.binding.recyclerAmountNameText.setText(amount);
        holder.binding.recyclerDateText.setText(stock.getBuyDate());
        holder.binding.recyclerStockNameText.setText(stock.getName());
        holder.binding.recyclerDateSellText.setText(stock.getSellDate());
        String formattedYuzde = "%"+String.format("%.2f",stock.getYuzde());
        holder.binding.yuzde.setText(formattedYuzde);

        double profitAndLoss = stock.getProfitAndLoss();
        Log.d("Goster","kar zarar: "+stock.getProfitAndLoss());
        String prt = String.format("%.2f", profitAndLoss);
        holder.binding.recyclerKarZararNameText.setText(prt + " TL");
        // Veri geldiğinde renklendirme işlemi yap
        if (stockArrayList.size() > 0) {
            //holder.binding.recyclerKarZararNameText.setText(String.valueOf(stock.getProfitAndLoss())+" TL");
            String color = stock.getColor(stock.getProfitAndLoss());

            if (Objects.equals(color, "red")) {
                holder.binding.recyclerKarZararNameText.setTextColor(context.getColor(R.color.red));
                holder.binding.yuzde.setTextColor(context.getColor(R.color.red));
            } else if (Objects.equals(color, "green")) {
                holder.binding.recyclerKarZararNameText.setTextColor(context.getColor(R.color.green));
                holder.binding.yuzde.setTextColor(context.getColor(R.color.green));
            }
        } else {
            // Veri yoksa metni temizle veya istediğiniz bir mesajı göster
            holder.binding.recyclerKarZararNameText.setText("");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StockDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", stock.getId());
                bundle.putString("name",stock.getName());
                bundle.putDouble("pieces",stock.getPieces());
                bundle.putString("buyDate",stock.getBuyDate());
                bundle.putString("sellDate",stock.getSellDate());
                bundle.putDouble("stockPriceBuy",stock.getStockPriceBuy());
                bundle.putDouble("stockPriceSell",stock.getStockPriceSell());
                bundle.putDouble("amount",stock.getAmount());
                bundle.putDouble("profitAndLoss",stock.getProfitAndLoss());
                bundle.putDouble("komisyon",stock.getKomisyon());
                bundle.putDouble("total",stock.getTotalAmount());
                bundle.putDouble("yuzde",stock.getYuzde());
                bundle.putDouble("ortMaliyet",stock.getOrtMaliyet());
                bundle.putDouble("sellPieces",stock.getSellPieces());
                intent.putExtra("userData", bundle);
                context.startActivity(intent);
            }
        });


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
                        Bundle bundle=new Bundle();
                        bundle.putInt("id",stock.getId());
                        bundle.putString("name",stock.getName());
                        bundle.putDouble("pieces",stock.getPieces());
                        bundle.putString("buyDate",stock.getBuyDate());
                        bundle.putString("sellDate",stock.getSellDate());
                        bundle.putDouble("stockPriceBuy",stock.getStockPriceBuy());
                        bundle.putDouble("stockPriceSell",stock.getStockPriceSell());
                        bundle.putDouble("amount",stock.getAmount());
                        bundle.putDouble("profitAndLoss",stock.getProfitAndLoss());
                        bundle.putDouble("komisyon",stock.getKomisyon());
                        bundle.putDouble("total",stock.getTotalAmount());
                        bundle.putDouble("yuzde",stock.getYuzde());
                        bundle.putDouble("ortMaliyet",stock.getOrtMaliyet());
                        bundle.putDouble("sellPieces",stock.getSellPieces());
                        try {
                            //Sil menü
                            if(menuItem.getItemId()==R.id.flowDelete){
                                AlertDialog.Builder alert= new AlertDialog.Builder(context);
                                alert.setTitle("Hisseyi sil");
                                alert.setMessage("Silmek istediğinizden emin misiniz?");
                                alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
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
                                            ((MainActivity) context).getYuzde();

                                        }
                                        else{
                                            Toast.makeText(context,"İşlem başarısız",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                alert.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                alert.show();
                            }
                            else if(menuItem.getItemId()==R.id.flowBuy){
                                //alma ekranı

                                Intent intent=new Intent(context, BuyActivity.class);
                                intent.putExtra("userData",bundle);
                                context.startActivity(intent);
                                notifyDataSetChanged();
                                ((MainActivity) context).getTotalPortfolio();
                                ((MainActivity) context).getProfitLoss();
                                ((MainActivity) context).getYuzde();

                            }
                            else if(menuItem.getItemId()==R.id.flowDSell){
                                //Satış ekranı
                                Intent intent=new Intent(context, SellActivity.class);
                                intent.putExtra("userData",bundle);
                                context.startActivity(intent);
                                notifyDataSetChanged();
                                ((MainActivity) context).getTotalPortfolio();
                                ((MainActivity) context).getProfitLoss();
                                ((MainActivity) context).getYuzde();

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
