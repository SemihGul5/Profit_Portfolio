package com.example.profitportfolio.fragment;

import static android.content.Intent.getIntent;
import static com.example.profitportfolio.DbHelper.TABLENAME;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.profitportfolio.BuyActivity;
import com.example.profitportfolio.DbHelper;
import com.example.profitportfolio.MainActivity;
import com.example.profitportfolio.R;
import com.example.profitportfolio.databinding.FragmentBuyBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class BuyFragment extends Fragment {
    private FragmentBuyBinding binding;
    int id;
    SQLiteDatabase database;
    DbHelper dbHelper;
    String dateSell,olddateBuy,buyNewDate,oldSatisTutari,oldsellPrice;
    double buyPrice,amount,komisyon,topAdet,calcOrt,calcAmount,calcKomisyon,calcTotal,maliyetKomisyon,karZarar,yuzde,pieces;
    double oldTotal,oldyuzde,oldbuyPrice,oldKomisyon=0,oldAmount,oldbuyPieces,oldortMaliyet,oldprofitAndLoss,oldSellPieces,oldKalanAdet,oldmaliyetKomisyon;
    public BuyFragment() {
        // Required empty public constructor
    }

    public static BuyFragment newInstance(String param1, String param2) {
        BuyFragment fragment = new BuyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper= new DbHelper(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBuyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Fragment'ın bulunduğu aktiviteyi al
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // ActionBar'ı veya Toolbar'ı al
        androidx.appcompat.app.ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar!=null){
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            // Başlık değiştirme işlemi
            actionBar.setTitle("Satın Al");
        }


        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getSelectedData();
        dateBuyTextCalendar();
        updateData();

    }
    private void getSelectedData() {
        /*Bundle bundle = getIntent().getBundleExtra("userData");
        if (bundle != null) {
            Log.d("BuyActivity", "Bundle içeriği: " + bundle.toString());
            id = bundle.getInt("id");
            binding.BuyStockNameText.setText(bundle.getString("name"));
            oldbuyPieces = bundle.getDouble("pieces");
            olddateBuy = bundle.getString("buyDate");
            dateSell = bundle.getString("sellDate");
            oldbuyPrice = bundle.getDouble("stockPriceBuy");


            String gelen = bundle.getString("stockPriceSell");
            if (gelen.isEmpty()||gelen.equals("null"))
            {
                gelen="0";
                oldsellPrice= "";
            }
            else{
                oldsellPrice= gelen;
            }

            oldAmount = bundle.getDouble("amount");
            oldprofitAndLoss = bundle.getDouble("profitAndLoss");
            oldKomisyon = bundle.getDouble("komisyon");
            oldTotal = bundle.getDouble("total");
            oldyuzde = bundle.getDouble("yuzde");
            oldortMaliyet = bundle.getDouble("ortMaliyet");
            oldSellPieces = bundle.getDouble("sellPieces");
            oldKalanAdet=bundle.getDouble("kalanAdet");
            oldmaliyetKomisyon=bundle.getDouble("maliyetKomisyon");
            String stutar = bundle.getString("satisTutari");
            if (stutar.equals("")||stutar.equals("null"))
            {
                stutar="";
                oldSatisTutari="";
            }
            else{
                oldSatisTutari= String.valueOf(Double.parseDouble(stutar));
            }
        } else {

            Toast.makeText(getContext(), "Veri alınamadı. Hata!", Toast.LENGTH_SHORT).show();
        }*/
    }
    private void updateData() {
        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (
                        binding.BuyPiecesText.getText().toString().isEmpty() ||
                                binding.BuyPriceText.getText().toString().isEmpty()||
                                binding.komisyonText.getText().toString().isEmpty() ||
                                binding.BuyDateBuyText.getText().toString().isEmpty()
                )
                {
                    Snackbar.make(v, "Tüm alanları doldurunuz.", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    try {
                        calculateAmountAndProfitLoss();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name",binding.BuyStockNameText.getText().toString());
                        contentValues.put("pieces",topAdet);
                        contentValues.put("buyDate",buyNewDate);
                        contentValues.put("sellDate",dateSell);
                        contentValues.put("stockPriceBuy",calcOrt);
                        contentValues.put("stockPriceSell",oldsellPrice);
                        contentValues.put("amount",calcAmount);
                        contentValues.put("profitAndLoss",karZarar);
                        contentValues.put("komisyon",calcKomisyon);
                        contentValues.put("total",calcTotal);
                        contentValues.put("yuzde",yuzde);
                        contentValues.put("ortMaliyet",calcOrt);
                        contentValues.put("sellPieces",oldSellPieces);
                        contentValues.put("kalanAdet",topAdet);
                        contentValues.put("satisTutari",oldSatisTutari);
                        contentValues.put("maliyetKomisyon",maliyetKomisyon);
                        database = dbHelper.getWritableDatabase();
                        long l= database.update(TABLENAME,contentValues,"id="+id,null);

                        if (l != -1) {
                            Toast.makeText(getContext(), "Satın Alım İşlemi Başarılı", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(getContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                        } else {
                            Toast.makeText(getContext(), "Kayıt Başarısız", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void calculateAmountAndProfitLoss() {
        try {

            komisyon = Double.parseDouble(binding.komisyonText.getText().toString());
            buyPrice = Double.parseDouble(binding.BuyPriceText.getText().toString());
            pieces = Double.parseDouble(binding.BuyPiecesText.getText().toString());
            buyNewDate = binding.BuyDateBuyText.getText().toString();

            topAdet=pieces+oldKalanAdet;//toplam adet
            double adet=topAdet+oldSellPieces;
            amount=(buyPrice*pieces);//girilen tutar

            calcAmount=amount+oldAmount;//eski + yeni tutar

            calcKomisyon=komisyon+oldKomisyon;//komisyon toplamı eski + yeni
            calcTotal=oldTotal+amount;//toplam tutar eski + yeni
            calcOrt=(calcAmount)/adet;

            maliyetKomisyon=oldmaliyetKomisyon+komisyon+amount;

            karZarar=oldprofitAndLoss-komisyon;

            yuzde=(karZarar/(calcKomisyon+calcAmount))*100;


            binding.amountText.setText(String.format("%.2f", amount));
            binding.hesaplananText.setText(String.format("%.2f", calcOrt));

        } catch (Exception e) {
            Toast.makeText(getContext(), "Hesaplama sırasında bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void dateBuyTextCalendar() {
        binding.BuyDateBuyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                binding.BuyDateBuyText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }
}