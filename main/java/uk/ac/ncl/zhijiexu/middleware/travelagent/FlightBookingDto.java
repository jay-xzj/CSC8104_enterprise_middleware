package uk.ac.ncl.zhijiexu.middleware.travelagent;

import java.io.Serializable;
import java.util.Date;

/**
 * <p> Simple POJO representing HotelBooking objects </p>
 * 
 * @author JayXu
 * @since 2019/11/21
 */
public class FlightBookingDto implements Serializable {

    private static final long serialVersionUID = -6134915266450157158L;
    private Integer id;
    private Flight flight;
    private FlightCustomer flightCustomer;
    private Date bookingDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public FlightCustomer getFlightCustomer() {
        return flightCustomer;
    }

    public void setFlightCustomer(FlightCustomer flightCustomer) {
        this.flightCustomer = flightCustomer;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
