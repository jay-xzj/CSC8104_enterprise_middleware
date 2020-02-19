package uk.ac.ncl.zhijiexu.middleware.taxi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import uk.ac.ncl.zhijiexu.middleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p> Simple POJO representing Taxi objects (A commodity) </p>
 * @author JayXu
 * @date 2019/11/02 17:23
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Taxi.FIND_ALL, query = "SELECT t FROM Taxi t ORDER BY t.registration ASC, t.seatsNumber ASC"),
        @NamedQuery(name = Taxi.FIND_BY_REGISTRATION, query = "SELECT c FROM Taxi c WHERE c.registration =:registration")
})
@XmlRootElement
@Table(name = "taxi", uniqueConstraints = @UniqueConstraint(columnNames = "registration"))
public class Taxi implements Serializable {

    private static final long serialVersionUID = -5517257819038588364L;

    public static final String FIND_ALL = "Taxi.findAll";
    public static final String FIND_BY_REGISTRATION = "Taxi.findByRegistration";

    @Id
    @ApiModelProperty(example = "101")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @ApiModelProperty(example = "UE89UKK")
    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z]{3}$", message = "Please use a alpha-numerical string which is 7 characters in length. For example, 'UU89UUK'.")
    @Column(name = "registration")
    private String registration;

    @NotNull
    @Min(2)
    @Max(20)
    @ApiModelProperty(example = "5")
    @Column(name = "seats_number")
    private Integer seatsNumber;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.REMOVE}, fetch = FetchType.EAGER,mappedBy = "taxi")
    //@JoinColumn(name="taxi_id")
    private Set<Booking> bookings = new HashSet<Booking>();

    //@JsonProperty
    public Long getId() {
        return id;
    }

    //@JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public Integer getSeatsNumber() {
        return seatsNumber;
    }

    public void setSeatsNumber(Integer seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Taxi taxi = (Taxi) o;
        return seatsNumber == taxi.seatsNumber &&
                id.equals(taxi.id) &&
                registration.equals(taxi.registration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registration, seatsNumber);
    }

    @Override
    public String toString() {
        return "Taxi{" +
                "id=" + id +
                ", registration='" + registration + '\'' +
                ", seatsNumber=" + seatsNumber +
                '}';
    }
}
