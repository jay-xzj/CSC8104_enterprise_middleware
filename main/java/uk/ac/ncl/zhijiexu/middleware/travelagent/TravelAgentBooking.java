package uk.ac.ncl.zhijiexu.middleware.travelagent;

import uk.ac.ncl.zhijiexu.middleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>  </p>
 *
 * <p>
 * When storing your TravelAgentBooking locally, as with part two,
 * you can link the Booking for your local commodity using Entity relationship @Annotations,
 * and simply store the ID’s of the upstream bookings locally.
 * </p>
 * 
 * @author JayXu
 * @since 2019/11/20
 */
@Entity
@NamedQueries({
		@NamedQuery(name = TravelAgentBooking.FIND_ALL, query = "SELECT c FROM TravelAgentBooking c ORDER BY c.id ASC") })
@XmlRootElement
@Table(name = "travel_agent_booking", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class TravelAgentBooking implements Serializable {

	private static final long serialVersionUID = 6421738597986002480L;
	public static final String FIND_ALL = "TravelAgentBooking.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "taxi_booking_id", nullable = false)
	private Booking taxiBooking;

	@JoinColumn(name = "hotel_booking_id", nullable = false)
	private HotelBooking hotelBooking;

	@JoinColumn(name = "flight_booking_id", nullable = false)
	private FlightBooking flightBooking;

	@NotNull(message = "Booking date could not be empty")
	@Future(message = "Booking date should be in the future")
	@Column(name = "agent_booking_date")
	private Date agentBookingDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public HotelBooking getHotelBooking() {
		return hotelBooking;
	}

	public void setHotelBooking(HotelBooking hotelBooking) {
		this.hotelBooking = hotelBooking;
	}

	public FlightBooking getFlightBooking() {
		return flightBooking;
	}

	public void setFlightBooking(FlightBooking flightBooking) {
		this.flightBooking = flightBooking;
	}

	public Booking getTaxiBooking() {
		return taxiBooking;
	}

	public void setTaxiBooking(Booking taxiBooking) {
		this.taxiBooking = taxiBooking;
	}

	public Date getAgentBookingDate() {
		return agentBookingDate;
	}

	public void setAgentBookingDate(Date agentBookingDate) {
		this.agentBookingDate = agentBookingDate;
	}
}
