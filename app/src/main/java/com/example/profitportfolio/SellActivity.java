package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
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
    String dateSell,olddateBuy,sellNewDate;
    double pieces;
    double sellPrice,amount,komisyon,topAdet,calcOrt,calcAmount,calcKomisyon,calcTotal,calcProfitAndLoss,calcYuzde,sellPieces;
    double oldsellPrice,oldTotal,oldyuzde,oldbuyPrice,oldKomisyon=0,oldAmount,oldbuyPieces,oldortMaliyet,oldprofitAndLoss,calcSell,oldSellPieces;

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
        binding.adetBilgiText.setText("Satılabilir adet: "+String.valueOf(oldbuyPieces) );
        if(oldbuyPieces==0){
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
                        if(oldbuyPieces<pieces){
                            Toast.makeText(SellActivity.this, "En fazla "+oldbuyPieces+" tane satım yapabilirsiniz", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("name",binding.SellStockNameText.getText().toString());
                            contentValues.put("pieces",topAdet);
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
                            database = dbHelper.getWritableDatabase();
                            long l= database.update(TABLENAME,contentValues,"id="+id,null);

                            if (l != -1) {
                                Toast.makeText(SellActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();


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
            pieces = Integer.parseInt(binding.SellPiecesText.getText().toString());
            sellNewDate = binding.SellDateSellText.getText().toString();


                topAdet=oldbuyPieces-pieces;

                amount=(sellPrice*pieces)+komisyon;
                calcKomisyon=komisyon+oldKomisyon;
                calcSell=amount/pieces;

                double x=(pieces*oldbuyPrice)+oldKomisyon;

                calcProfitAndLoss=oldprofitAndLoss+(amount-x);
                calcTotal=oldTotal+calcProfitAndLoss;
                calcYuzde=(calcProfitAndLoss/oldAmount)*100;
                sellPieces=pieces;


                binding.amountText.setText(String.format("%.2f", amount));



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
            if (stockPriceSell != null) {
                oldsellPrice = Double.parseDouble(stockPriceSell);
            }
            else{
                oldsellPrice=0;
            }
            oldAmount = bundle.getDouble("amount");
            oldprofitAndLoss = bundle.getDouble("profitAndLoss");
            oldKomisyon = bundle.getDouble("komisyon");
            oldTotal = bundle.getDouble("total");
            oldyuzde = bundle.getDouble("yuzde");
            oldortMaliyet = bundle.getDouble("ortMaliyet");
            oldSellPieces = bundle.getDouble("sellPieces");
        } else {

            Toast.makeText(this, "Veri alınamadı. Hata!", Toast.LENGTH_SHORT).show();
        }
    }
}