package uk.ac.ncl.zhijiexu.middleware.travelagent;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author JayXu
 * @description:
 * @date 2019/11/22 13:47
 */
public class HotelBooking implements Serializable {

    private static final long serialVersionUID = -2392960809919607304L;
    private Long id;
    private Long hotelId;
    private Date bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
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
        HotelBooking that = (HotelBooking) o;
        return getId().equals(that.getId()) &&
                getHotelId().equals(that.getHotelId()) &&
                getBookingDate().equals(that.getBookingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getHotelId(), getBookingDate());
    }

    @Override
    public String toString() {
        return "HotelBooking{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
