package co.com.sergio.catalogodistritodo.categoryAdmin.othersAdmin;

public class Other {

    private String image;
    private String name;
    private String price;
    private String description;

    /** Constructor */

    public Other(){}

    public Other(String image, String name, String price, String description) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    /** Getter and Setter **/
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
