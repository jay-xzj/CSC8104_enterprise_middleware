package uk.ac.ncl.zhijiexu.middleware.guestbooking;

import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p> POJO representing guestBooking objects.</p>
 *
 * This bean should not be a Hibernate @Entity
 * Its purpose is simply to allow the Jackson JSON library to deserialize a request Body containing both a Customer and a Booking.
 * @author JayXu
 * @date 2019/11/02 17:58
 */
@XmlRootElement
public class GuestBooking implements Serializable{

    private static final long serialVersionUID = 5337244957335716624L;

    private Customer customer;

    private Long taxiId;

    private Date bookingDate;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
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
        GuestBooking that = (GuestBooking) o;
        return getCustomer().equals(that.getCustomer()) &&
                getTaxiId().equals(that.getTaxiId()) &&
                getBookingDate().equals(that.getBookingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomer(), getTaxiId(), getBookingDate());
    }

    @Override
    public String toString() {
        return "GuestBooking{" +
                "customer=" + customer +
                ", taxiId=" + taxiId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
