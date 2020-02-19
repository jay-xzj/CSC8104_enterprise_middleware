package uk.ac.ncl.zhijiexu.middleware.travelagent;

/**
 * <p> BookingTypeEnum Different booking service type enumeration. </p>
 * @author JayXu
 * @since 2019/11/20 18:35
 */
public enum BookingTypeEnum {

    FLIGHT_BOOKING("http://api-deployment-csc8104-190218087.b9ad.pro-us-east-1.openshiftapps.com"),
    HOTEL_BOOKING("http://api-deployment-csc8104-180682719.b9ad.pro-us-east-1.openshiftapps.com");

    private String bookingUrl;

    BookingTypeEnum(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }
}
