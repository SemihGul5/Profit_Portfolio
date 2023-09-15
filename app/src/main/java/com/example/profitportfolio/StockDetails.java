package com.example.profitportfolio;

import static com.example.profitportfolio.DbHelper.TABLENAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.databinding.ActivityMainBinding;
import com.example.profitportfolio.databinding.ActivityStockDetailsBinding;

import java.util.Calendar;

public class StockDetails extends AppCompatActivity {
    private ActivityStockDetailsBinding binding;
    SQLiteDatabase database;
    DbHelper dbHelper;

    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityStockDetailsBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        dbHelper= new DbHelper(this);

        editData();

        updateData();
    }
    private void dateSellTextCalendar() {
        binding.dateSellText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        StockDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                binding.dateSellText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void dateBuyTextCalendar() {
        binding.dateBuyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        StockDetails.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                binding.dateBuyText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {






















            @Override
            public void onClick(View v) {
                try {
                    ContentValues contentValues= new ContentValues();
                    contentValues.put("name",binding.stockNameText.getText().toString());
                    contentValues.put("pieces",binding.piecesText.getText().toString());
                    contentValues.put("stockPriceBuy",binding.buyPriceText.getText().toString());
                    contentValues.put("stockPriceSell",binding.sellPriceText.getText().toString());
                    contentValues.put("komisyon",binding.komisyonText.getText().toString());
                    contentValues.put("buyDate",binding.dateBuyText.getText().toString());
                    contentValues.put("sellDate",binding.dateSellText.getText().toString());
                    contentValues.put("amount",binding.amountText.getText().toString());
                    contentValues.put("total",binding.totalAmountText.getText().toString());
                    contentValues.put("profitAndLoss",binding.profitLossText.getText().toString());

                    database=dbHelper.getWritableDatabase();

                    long l= database.update(TABLENAME,contentValues,"id="+id,null);
                    if(l!=-1){
                        Toast.makeText(StockDetails.this,"Güncellendi",Toast.LENGTH_SHORT).show();
                        Intent intent= new Intent(StockDetails.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Toast.makeText(StockDetails.this,"Hata",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void editData() {
        if(getIntent().getBundleExtra("userData")!=null){
            Bundle bundle= getIntent().getBundleExtra("userData");
            assert bundle != null;
            binding.stockNameText.setText(bundle.getString("name"));
            binding.piecesText.setText(String.valueOf(bundle.getInt("pieces")) );
            binding.buyPriceText.setText(String.valueOf(bundle.getDouble("stockPriceBuy")) );
            binding.sellPriceText.setText(String.valueOf(bundle.getDouble("stockPriceSell")) );
            binding.komisyonText.setText(String.valueOf(bundle.getDouble("komisyon")) );
            binding.dateBuyText.setText(bundle.getString("buyDate"));
            binding.dateSellText.setText(bundle.getString("sellDate"));
            binding.amountText.setText(String.valueOf(bundle.getDouble("amount")) );
            binding.totalAmountText.setText(String.valueOf(bundle.getDouble("total")) );
            binding.profitLossText.setText(String.valueOf(bundle.getDouble("profitAndLoss")) );
            id=bundle.getInt("id");
            binding.saveButton.setText("Güncelle");
        }
    }
}