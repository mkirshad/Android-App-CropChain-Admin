package ae.cropchain;

public class Product {
    long Id;
    String Name;
    int Rate;
    String RateUpdatedAt;
    long RateUpdatedByUserId;
    int IsActive;
    int SortOrder;

    public Product(){

    }

    public Product(long id, String name, int rate, String rateUpdatedAt, long rateUpdatedByUserId, int isActive, int sortOrder) {
        Id = id;
        Name = name;
        Rate = rate;
        RateUpdatedAt = rateUpdatedAt;
        RateUpdatedByUserId = rateUpdatedByUserId;
        IsActive = isActive;
        SortOrder = sortOrder;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getRate() {
        return Rate;
    }

    public void setRate(int rate) {
        Rate = rate;
    }

    public String getRateUpdatedAt() {
        return RateUpdatedAt;
    }

    public void setRateUpdatedAt(String rateUpdatedAt) {
        RateUpdatedAt = rateUpdatedAt;
    }

    public long getRateUpdatedByUserId() {
        return RateUpdatedByUserId;
    }

    public void setRateUpdatedByUserId(long rateUpdatedByUserId) {
        RateUpdatedByUserId = rateUpdatedByUserId;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
    }

    public int getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(int sortOrder) {
        SortOrder = sortOrder;
    }
}
