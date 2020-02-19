package uk.ac.ncl.zhijiexu.middleware.booking;

import io.swagger.annotations.ApiModelProperty;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.taxi.Taxi;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p> Booking. (A relationship between a user and a commodity, with additional attributes) </p>
 *
 * You may leave the customer field of a Booking object’s JSON blank, then use setCustomer(Customer c)
 * to set a Booking customer to be a newly created Customer object, before attempting to persist the Booking itself.
 *
 * @author JayXu
 * @date 2019/11/02 18:00
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL, query = "SELECT b FROM Booking b ORDER BY b.customer.id ASC,b.taxi.id ASC,b.bookingDate DESC"),
        @NamedQuery(name = Booking.FIND_BY_CUSTOMER, query = "SELECT b FROM Booking b WHERE b.customer.id = :customerId"),
        @NamedQuery(name = Booking.FIND_BY_TAXI, query = "SELECT b FROM Booking b WHERE b.taxi.id = :taxiId")
})
@XmlRootElement
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"taxi_id","booking_date"}))
public class Booking implements Serializable {

    private static final long serialVersionUID = -9130775688081656898L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_CUSTOMER = "Booking.findByCustomer";
    public static final String FIND_BY_TAXI = "Booking.findByTaxi";

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "taxi_id")
    private Taxi taxi;

    @ApiModelProperty(example = "2020-12-24")
    @NotNull(message = "Booking date could not be empty")
    @Future(message = "Booking date should be in the future")
    @Column(name = "booking_date")
    @Temporal(TemporalType.DATE)
    private Date bookingDate;

    /*@JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE)
    private TravelAgentBooking travelAgentBooking;*/

    //@JsonProperty
    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
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
        Booking booking = (Booking) o;
        return getId().equals(booking.getId()) &&
                getCustomer().equals(booking.getCustomer()) &&
                getTaxi().equals(booking.getTaxi()) &&
                getBookingDate().equals(booking.getBookingDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCustomer(), getTaxi(), getBookingDate());
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customer=" + customer +
                ", taxi=" + taxi +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
