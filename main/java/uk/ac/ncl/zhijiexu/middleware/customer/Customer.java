package uk.ac.ncl.zhijiexu.middleware.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import uk.ac.ncl.zhijiexu.middleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * <p> Simple POJO representing Customer objects  (A user) </p>
 *
 * @author JayXu
 * @date 2019/11/02 16:22
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.name ASC"),
        @NamedQuery(name = Customer.FIND_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email")
})
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable {

    private static final long serialVersionUID = -7716693148346943487L;

    public static final String FIND_ALL = "Customer.findAll";
    public static final String FIND_BY_EMAIL = "Contact.findByEmail";

    @Id
    @ApiModelProperty(example = "1003")
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    @ApiModelProperty(example = "Jay Xu")
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^([A-Za-z]+\\s?)*[A-Za-z]$", message = "Please use a name without numbers or specials")
    @Column(name = "name")
    private String name;

    @NotNull
    @NotEmpty
    @ApiModelProperty(example = "jay.X39@newcastle.ac.uk")
    @Email(message = "The email address must be in the format of name@domain.com")
    @Column(name = "email")
    private String email;

    @NotNull
    @ApiModelProperty(example = "07743764944")
    @Pattern(regexp = "^0\\d{10}$", message = "Please use a sequence of numbers start with 0 and 11 digits in length.")
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.REMOVE}, mappedBy = "customer")
    //@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //@JoinColumn(name="customer_id")
    private Set<Booking> bookings = new HashSet<Booking>();

    //@JsonProperty
    public Long getId() {
        return id;
    }

    //@JsonIgnore
    public void setId(Long id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public boolean equals(Object obj) {
        //reflexivity
        if (this == obj) return true;
        //non-nullity
        if (!(obj instanceof Customer)) return false;
        //consistency
        Customer c = (Customer) obj;
        return (this.id == null ? c.id == null : c.id.equals(this.id))
                && (this.name == null ? c.name == null : c.name.equals(this.name))
                && (this.email == null ? c.email == null : c.email.equals(this.email))
                && (this.phoneNumber == null ? c.phoneNumber == null : c.phoneNumber.equals(this.phoneNumber));
    }

    @Override
    public int hashCode() {
        int hc = 1;
        hc = 31 * hc + (this.id == null ? 0 : this.id.hashCode())
                + (this.name == null ? 0 : this.name.hashCode())
                + (this.email == null ? 0 : this.email.hashCode())
                + (this.phoneNumber == null ? 0 : this.phoneNumber.hashCode());
        return 31 * hc;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getId() +
                ", name='" + getName()+ '\'' +
                ", email='" + getEmail() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                '}';
    }
}
