package uk.ac.ncl.zhijiexu.middleware.travelagent;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import uk.ac.ncl.zhijiexu.middleware.booking.Booking;
import uk.ac.ncl.zhijiexu.middleware.booking.BookingService;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p> TravelAgentService </p>
 * 
 * @author JayXu
 * @since 2019/11/20
 */
@Dependent
public class TravelAgentBookingService {

	@Inject
	private @Named("logger") Logger log;

	@Inject
	private TravelAgentBookingRepository crud;

	@Inject
	private BookingService taxiBookingService;

	private ResteasyClient client;

	public TravelAgentBookingService(ResteasyClient client) {
		client = new ResteasyClientBuilder().build();
	}

	public TravelAgentBooking create(TravelAgentBooking travelAgentBooking) throws Exception {

		//Create client service instance to make REST requests to upstream service
		ResteasyWebTarget target = client.target(BookingTypeEnum.FLIGHT_BOOKING.getBookingUrl());
		FlightBookingService flightBookingService = target.proxy(FlightBookingService.class);

		ResteasyWebTarget target1 = client.target(BookingTypeEnum.HOTEL_BOOKING.getBookingUrl());
		HotelBookingService hotelBookingService = target1.proxy(HotelBookingService.class);

		FlightBookingDto flightBookingDto = new FlightBookingDto();
		FlightCustomer flightCustomer = new FlightCustomer();
		//Flight flight = new Flight();

		HotelBookingDto hotelBookingDto = new HotelBookingDto();
		HotelCustomer hotelCustomer = new HotelCustomer();
		//Hotel hotel = new Hotel();


		//get TaxiBooking object
		Booking taxiBooking = travelAgentBooking.getTaxiBooking();
		Booking booking = taxiBookingService.create(taxiBooking);

		//get flightBookingId
		FlightBooking fb = travelAgentBooking.getFlightBooking();
		//get flightId from flightBooking
		Long flightId = fb.getFlightId();

		//find ALREADY exists flight
		Flight flightById = flightBookingService.getFlightById(flightId);

		Customer customer = taxiBooking.getCustomer();
		flightCustomer.setName(customer.getName());
		flightCustomer.setEmail(customer.getEmail());
		flightCustomer.setPhoneNumber(customer.getPhoneNumber());

		flightBookingDto.setFlightCustomer(flightCustomer);
		flightBookingDto.setBookingDate(taxiBooking.getBookingDate());
		flightBookingDto.setFlight(flightById);

		//create flightBooking
		flightBookingService.createFlightBooking(flightBookingDto);

		//get hotelBookingId
		HotelBooking hb = travelAgentBooking.getHotelBooking();
		Long hotelId = hb.getHotelId();

		//find ALREADY existed hotel
		Hotel hotelById = hotelBookingService.getHotelById(hotelId);

		//set parameters to hotelCustomer
		hotelCustomer.setName(customer.getName());
		hotelCustomer.setEmail(customer.getEmail());
		hotelCustomer.setPhoneNumber(customer.getPhoneNumber());

		//set parameters to hotelBooking
		hotelBookingDto.setHotelCustomer(hotelCustomer);
		hotelBookingDto.setBookingDate(taxiBooking.getBookingDate());
		hotelBookingDto.setHotel(hotelById);

		//create hotelBooking
		hotelBookingService.createHotelBooking(hotelBookingDto);

		travelAgentBooking.setAgentBookingDate(taxiBooking.getBookingDate());
		travelAgentBooking.setTaxiBooking(booking);

		// Write the contact to the database.
		return crud.create(travelAgentBooking);
	}

	TravelAgentBooking delete(TravelAgentBooking travelAgentBooking) throws Exception {
		log.info("delete() - Deleting " + travelAgentBooking.toString());


		//Create client service instance to make REST requests to upstream service
		ResteasyWebTarget target = client.target(BookingTypeEnum.FLIGHT_BOOKING.getBookingUrl());
		FlightBookingService flightBookingService = target.proxy(FlightBookingService.class);

		ResteasyWebTarget target1 = client.target(BookingTypeEnum.HOTEL_BOOKING.getBookingUrl());
		HotelBookingService hotelBookingService = target1.proxy(HotelBookingService.class);

		TravelAgentBooking deletedContact = null;

		if (travelAgentBooking.getId() != null) {
			deletedContact = crud.delete(travelAgentBooking);
			flightBookingService.deleteFlightBooking(travelAgentBooking.getFlightBooking().getFlightId());
			hotelBookingService.deleteHotelBooking(travelAgentBooking.getHotelBooking().getHotelId());
		} else {
			log.info("delete() - No ID was found so can't Delete.");
		}

		return deletedContact;
	}

	public TravelAgentBooking findById(long id) {
		return crud.findById(id);
	}

	public List<TravelAgentBooking> findAllBookings() {
		return crud.findAllAgentBookings();
	}
}
