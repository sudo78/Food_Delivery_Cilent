package fastfood.foodapp.Model;

/**
 * Created by Sheraz on 12/29/2017.
 */

public class Category {
    private  String  Name;
    private  String Image;
    private  String exta;
    private  String extra;

    public Category() {
    }

    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

    public String getExta() {
        return exta;
    }

    public void setExta(String exta) {
        this.exta = exta;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
