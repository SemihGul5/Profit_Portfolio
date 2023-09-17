package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityBuyBinding;
import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class BuyActivity extends AppCompatActivity {
    private ActivityBuyBinding binding;
    int id;
    SQLiteDatabase database;
    DbHelper dbHelper;
    String dateBuy,dateSell;
    int pieces;
    double calc=0 ,x=0,piecesNew=0,p=0,sum=0,yuzde,buyPrice,totalAmount,profitLoss,amount,komisyon=0,sellPrice;
    double oldsellPrice,profitAndLoss,oldyuzde,buyPieces,oldbuyPrice,oldKomisyon,hesaplaKomisyon,oldAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityBuyBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        setTitle("Satın Al");
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);




        getSelectedData();
        dbHelper= new DbHelper(this);
        updateData();



    }
    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        binding.BuyPiecesText.getText().toString().isEmpty() ||
                        binding.BuyPriceText.getText().toString().isEmpty()||
                                binding.komisyonText.getText().toString().isEmpty()
                )
                {
                    Snackbar.make(v, "Gerekli alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    try {
                        calculateAmountAndProfitLoss();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", binding.BuyStockNameText.getText().toString());
                        contentValues.put("pieces", piecesNew);
                        contentValues.put("buyDate", dateBuy);
                        contentValues.put("sellDate", dateSell);
                        contentValues.put("stockPriceBuy", calc);
                        contentValues.put("stockPriceSell", sellPrice);
                        contentValues.put("amount",totalAmount );
                        contentValues.put("profitAndLoss", profitAndLoss);
                        contentValues.put("komisyon", hesaplaKomisyon);
                        contentValues.put("total", totalAmount);
                        contentValues.put("yuzde",yuzde);

                        database = dbHelper.getWritableDatabase();
                        long l= database.update(TABLENAME,contentValues,"id="+id,null);

                        if (l != -1) {
                            Toast.makeText(BuyActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();


                        } else {
                            Toast.makeText(BuyActivity.this, "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(BuyActivity.this, "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void calculateAmountAndProfitLoss() {
        try {

            String color="";
            Cursor cursor3= database.rawQuery("select komisyon from "+TABLENAME+" where id="+id+" ",null);
            if(cursor3.moveToFirst()){
                oldKomisyon=cursor3.getDouble(0);
            }
            cursor3.close();

                komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
                buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
                pieces = Integer.parseInt(binding.BuyPiecesText.getText().toString());
                x=(buyPrice*pieces)-komisyon;
                hesaplaKomisyon=oldKomisyon+komisyon;
                database=dbHelper.getReadableDatabase();
                Cursor cursor= database.rawQuery("select total from "+TABLENAME+" where id="+id+" ",null);
                if(cursor.moveToFirst()){
                    sum=cursor.getDouble(0);
                }
                cursor.close();
                database=dbHelper.getReadableDatabase();
                @SuppressLint("Recycle") Cursor cursor2= database.rawQuery("select pieces from "+TABLENAME+" where id="+id+" ",null);
                if(cursor2.moveToFirst()){
                    p=cursor2.getDouble(0);
                }
                cursor.close();
                piecesNew=p+pieces;
                calc=(sum+x)/piecesNew;
                totalAmount=sum+x;



            binding.amountText.setText(String.format("%.2f", x));
            binding.hesaplananText.setText(String.format("%.2f", calc));


        }
        catch (Exception e) {
            Toast.makeText(BuyActivity.this,"hata",Toast.LENGTH_SHORT).show();
        }

    }
    private void getSelectedData() {
        if(getIntent().getBundleExtra("userData")!=null){
            Bundle bundle= getIntent().getBundleExtra("userData");
            assert bundle != null;
            binding.BuyStockNameText.setText(bundle.getString("name"));
            buyPieces=(bundle.getInt("pieces")) ;
            oldbuyPrice=(bundle.getDouble("stockPriceBuy")) ;
            oldsellPrice=bundle.getDouble("stockPriceSell");
            oldKomisyon=(bundle.getDouble("komisyon"));
            dateBuy= (bundle.getString("buyDate"));
            dateSell=(bundle.getString("sellDate"));

            binding.oldAmountText.setText(String.valueOf(bundle.getDouble("total")) );
            profitAndLoss=(bundle.getDouble("profitAndLoss")) ;
            oldyuzde=bundle.getDouble("yuzde");
            id=bundle.getInt("id");

        }
    }

}