package fastfood.foodapp.Model;

/**
 * Created by Umer Sheraz on 2/18/2018.
 */

public class Rating {
    String userphone,foodid,ratevalue,comment;

    public Rating() {
    }

    public Rating(String userphone, String foodid, String ratevalue, String comment) {
        this.userphone = userphone;
        this.foodid = foodid;
        this.ratevalue = ratevalue;
        this.comment = comment;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getFoodid() {
        return foodid;
    }

    public void setFoodid(String foodid) {
        this.foodid = foodid;
    }

    public String getRatevalue() {
        return ratevalue;
    }

    public void setRatevalue(String ratevalue) {
        this.ratevalue = ratevalue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
