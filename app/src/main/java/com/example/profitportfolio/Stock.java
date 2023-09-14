package com.example.profitportfolio;

public class Stock {
    private int id;
    private String name;
    private int pieces;
    private String buyDate;
    private String sellDate;
    private double stockPriceBuy;
    private double stockPriceSell;
    private double amount;
    private  double profitAndLoss;
    private double komisyon;
    private static String color;



    public Stock(int id, String name, int pieces, String buyDate, String sellDate, double stockPriceBuy, double stockPriceSell, double amount, double profitAndLoss, double komisyon) {
        this.id = id;
        this.name = name;
        this.pieces = pieces;
        this.buyDate = buyDate;
        this.sellDate = sellDate;
        this.stockPriceBuy = stockPriceBuy;
        this.stockPriceSell = stockPriceSell;
        this.amount = amount;
        this.profitAndLoss = profitAndLoss;
        this.komisyon=komisyon;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor(double prof) {
        if(prof<0)
        {
            return color="red";
        }
        else{
            return color="green";
        }
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPieces() {
        return pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public String getSellDate() {
        return sellDate;
    }

    public void setSellDate(String sellDate) {
        this.sellDate = sellDate;
    }

    public double getStockPriceBuy() {
        return stockPriceBuy;
    }

    public void setStockPriceBuy(double stockPriceBuy) {
        this.stockPriceBuy = stockPriceBuy;
    }

    public double getStockPriceSell() {
        return stockPriceSell;
    }

    public void setStockPriceSell(double stockPriceSell) {
        this.stockPriceSell = stockPriceSell;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(double profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }
    public double getKomisyon() {
        return komisyon;
    }

    public void setKomisyon(double komisyon) {
        this.komisyon = komisyon;
    }
}
