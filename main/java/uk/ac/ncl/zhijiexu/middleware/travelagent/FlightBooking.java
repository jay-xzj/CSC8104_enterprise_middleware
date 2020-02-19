package uk.ac.ncl.zhijiexu.middleware.travelagent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author JayXu
 * @description:
 * @date 2019/11/22 13:47
 */
public class FlightBooking implements Serializable {

    private static final long serialVersionUID = 8999442664332337306L;
    private Long id;
    private Long flightId;
    private Date bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightBooking that = (FlightBooking) o;
        return getId().equals(that.getId()) &&
                getFlightId().equals(that.getFlightId()) &&
                getBookingDate().equals(that.getBookingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFlightId(), getBookingDate());
    }

    @Override
    public String toString() {
        return "FlightBooking{" +
                "id=" + id +
                ", flightId=" + flightId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
