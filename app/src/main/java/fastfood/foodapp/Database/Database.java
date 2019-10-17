package fastfood.foodapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fastfood.foodapp.Model.Order;

/**
 * Created by Sheraz on 1/1/2018.
 */

public class Database extends SQLiteAssetHelper {
    private static final  String DB_NAME="database.db";
    private static final  int DB_Version=1;



    public Database(Context context) {

        super(context, DB_NAME, null, DB_Version);


    }
    public List<Order> get_charts(){
        SQLiteDatabase db=getReadableDatabase();
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();

        String[] sqlselect={"ProductID","ProductName","Quantity","Price","Discount","Image"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c=qb.query(db,sqlselect,null,null,null,null,null);

         final List<Order> result=new ArrayList<>();
         if(c.moveToFirst()){
             do{
                 result.add(new Order(c.getString(c.getColumnIndex("ProductID")),
                         c.getString(c.getColumnIndex("ProductName")),
                         c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                         c.getString(c.getColumnIndex("Image"))));
             }while (c.moveToNext());
         }
         return  result;
    }
    public void addToChart(Order order){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO OrderDetail(ProductID,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s');",
                order.getProductID(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage()
        );
        db.execSQL(query);



    }
    public void cleanchart(){
        Log.d("cleanchart","cleanchart");
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }
    public void addFavourite(String foodid){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("INSERT INTO Favourite(FoodID) VALUES('%s');",foodid);
        db.execSQL(query);
    }
    public void removeFavourite(String foodid){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("DELETE FROM Favourite where FoodID='%s';",foodid);
        db.execSQL(query);
    }
    public boolean isFavourite(String foodid){
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT * FROM Favourite where FoodID='%s';",foodid);
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
       return true;
    }

    public int getcountcart() {
        int count=0;
        SQLiteDatabase db=getReadableDatabase();
        String query=String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToNext()){
            do{
                count=cursor.getInt(0);

            }while (cursor.moveToNext());
        }
        return count;
    }
}