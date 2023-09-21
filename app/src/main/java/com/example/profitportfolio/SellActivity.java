package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityBuyBinding;
import com.example.profitportfolio.databinding.ActivitySellBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Objects;

public class SellActivity extends AppCompatActivity {
    private ActivitySellBinding binding;
    int id;
    SQLiteDatabase database;
    DbHelper dbHelper;
    String dateSell,olddateBuy,sellNewDate,oldSatisTutari;
    double pieces,st;
    double sellPrice,komisyon,topAdet,calcKomisyon,calcTotal,calcProfitAndLoss,calcYuzde,sellPieces,maliyetKomisyon,oldMaliyetKomisyon;
    double oldsellPrice,oldTotal,oldyuzde,oldbuyPrice,oldKomisyon=0,oldAmount,oldbuyPieces,oldortMaliyet,oldprofitAndLoss,calcSell,oldSellPieces,oldKalanAdet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySellBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        setTitle("Hisse Sat");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);

        dbHelper= new DbHelper(this);
        getSelectedData();
        binding.adetBilgiText.setText("Satılabilir adet: "+String.valueOf(oldKalanAdet) );
        if(oldKalanAdet==0){
            Snackbar.make(view,"Bu hisseden satılabilir lot kalmamıştır!",Snackbar.LENGTH_INDEFINITE).show();
            binding.saveButton.setVisibility(View.INVISIBLE);
        }
        dateSellTextCalendar();
        updateData();


    }

    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        binding.SellPiecesText.getText().toString().isEmpty() ||
                                binding.SellPriceText.getText().toString().isEmpty()||
                                binding.komisyonText.getText().toString().isEmpty() ||
                                binding.SellDateSellText.getText().toString().isEmpty()
                )
                {
                    Snackbar.make(v, "Tüm alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    try {
                        calculateAmountAndProfitLoss();
                        if(oldKalanAdet<pieces){
                            Toast.makeText(SellActivity.this, "En fazla "+oldKalanAdet+" tane satım yapabilirsiniz", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name",binding.SellStockNameText.getText().toString());
                            contentValues.put("pieces",oldbuyPieces);
                            contentValues.put("buyDate",olddateBuy);
                            contentValues.put("sellDate",sellNewDate);
                            contentValues.put("stockPriceBuy",oldbuyPrice);
                            contentValues.put("stockPriceSell",calcSell);
                            contentValues.put("amount",oldAmount);
                            contentValues.put("profitAndLoss",calcProfitAndLoss);
                            contentValues.put("komisyon",calcKomisyon);
                            contentValues.put("total",calcTotal);
                            contentValues.put("yuzde",calcYuzde);
                            contentValues.put("ortMaliyet",oldortMaliyet);
                            contentValues.put("sellPieces",sellPieces);
                            contentValues.put("kalanAdet",topAdet);
                            contentValues.put("satisTutari",st);
                            contentValues.put("maliyetKomisyon",maliyetKomisyon);
                            database = dbHelper.getWritableDatabase();
                            long l= database.update(TABLENAME,contentValues,"id="+id,null);

                            if (l != -1) {
                                Toast.makeText(SellActivity.this, "Satış İşlemi Başarılı", Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(SellActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(SellActivity.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SellActivity.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void calculateAmountAndProfitLoss() {
        try {
            komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
            sellPrice = Double.parseDouble(binding.SellPriceText.getText().toString());
            pieces = Double.parseDouble(binding.SellPiecesText.getText().toString());
            sellNewDate = binding.SellDateSellText.getText().toString();


                topAdet=oldKalanAdet-pieces;//yeni adet
                sellPieces=oldSellPieces+pieces;//5
                double maliyet=(sellPrice*pieces);//yeni tutar//100
                double AlisMaliyeti=oldortMaliyet*pieces;
                calcKomisyon=komisyon+oldKomisyon;//0

                if(!oldSatisTutari.equals("null")){
                    st =maliyet+Double.parseDouble(oldSatisTutari);
                    calcSell=st/sellPieces;
                    calcProfitAndLoss=st-oldAmount-calcKomisyon;
                }
                else{
                    st=maliyet;
                    calcSell=maliyet/sellPieces;
                    calcProfitAndLoss=maliyet-AlisMaliyeti-calcKomisyon;
                }
                maliyetKomisyon=oldMaliyetKomisyon+komisyon;
                calcTotal=maliyetKomisyon+calcProfitAndLoss;



                calcYuzde=(calcProfitAndLoss/(maliyetKomisyon))*100;
                binding.amountText.setText(String.format("%.2f", maliyet));

        } catch (Exception e) {
            Toast.makeText(SellActivity.this, "Hesaplama sırasında bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void dateSellTextCalendar() {
        binding.SellDateSellText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SellActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                binding.SellDateSellText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }
    private void getSelectedData() {
        Bundle bundle = getIntent().getBundleExtra("userData");
        if (bundle != null) {
            Log.d("BuyActivity", "Bundle içeriği: " + bundle.toString());
            id = bundle.getInt("id");
            binding.SellStockNameText.setText(bundle.getString("name"));
            oldbuyPieces = bundle.getDouble("pieces");
            olddateBuy = bundle.getString("buyDate");
            dateSell = bundle.getString("sellDate");
            oldbuyPrice = bundle.getDouble("stockPriceBuy");
            String stockPriceSell = bundle.getString("stockPriceSell");
            if (stockPriceSell.isEmpty()||stockPriceSell.equals("null")) {
                oldsellPrice=0;
                stockPriceSell="0";
            }
            else{
                oldsellPrice = Double.parseDouble(stockPriceSell);
            }
            oldAmount = bundle.getDouble("amount");
            oldprofitAndLoss = bundle.getDouble("profitAndLoss");
            oldKomisyon = bundle.getDouble("komisyon");
            oldTotal = bundle.getDouble("total");
            oldyuzde = bundle.getDouble("yuzde");
            oldortMaliyet = bundle.getDouble("ortMaliyet");
            oldSellPieces = bundle.getDouble("sellPieces");
            oldKalanAdet = bundle.getDouble("kalanAdet");
            oldMaliyetKomisyon = bundle.getDouble("maliyetKomisyon");
            String satisTutar = bundle.getString("satisTutari");
            if (satisTutar.isEmpty()||satisTutar.equals("null")) {
                oldSatisTutari="null";
                satisTutar="";
            }
            else{
                oldSatisTutari = satisTutar;
            }
        } else {

            Toast.makeText(this, "Veri alınamadı. Hata!", Toast.LENGTH_SHORT).show();
        }
    }
}