package uk.ac.ncl.zhijiexu.middleware.travelagent;

import uk.ac.ncl.zhijiexu.middleware.booking.Booking;
import java.util.ArrayList;
import java.util.List;

public class Flight {


    private Long id;
    private String number;
    private String pointofDeparture;
    private String destination;

    private List<Booking> bookings = new ArrayList<Booking>();
    
	public Flight() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPointofDeparture() {
		return pointofDeparture;
	}

	public void setPointofDeparture(String pointofDeparture) {
		this.pointofDeparture = pointofDeparture;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
    
	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}
    

    
}
