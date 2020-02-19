package uk.ac.ncl.zhijiexu.middleware.travelagent;

import io.swagger.annotations.*;
import uk.ac.ncl.zhijiexu.middleware.booking.UniqueBookingException;
import uk.ac.ncl.zhijiexu.middleware.customer.UniqueEmailException;
import uk.ac.ncl.zhijiexu.middleware.util.RestServiceException;
import uk.ac.ncl.zhijiexu.middleware.booking.Booking;
import uk.ac.ncl.zhijiexu.middleware.booking.BookingRestService;
import uk.ac.ncl.zhijiexu.middleware.booking.BookingService;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerService;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p> TravelAgentRestService RESTFul APIs for TravelAgentBooking </p>
 * 
 * @author JayXu
 * @since 2019/11/20
 */
@Path("/travelAgent")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/travelAgent", description = "Travel agent manage all kinds of bookings")
@Stateless
public class TravelAgentBookingRestService {

	@Inject
	private @Named("logger") Logger log;

	@Inject
	private TravelAgentBookingService bookingService;

	@Inject
	private UserTransaction userTransaction;

	@POST
	@ApiOperation(value = "Add a new Booking to the database")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Booking created successfully."),
			@ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
			@ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request") })
	public Response createTravelAgentBooking(
			@ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
                    TravelAgentBooking travelAgentBooking) {

		if (travelAgentBooking == null) {
			throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
		}
		Response.ResponseBuilder builder;
		try{
			userTransaction.begin();
			bookingService.create(travelAgentBooking);
			userTransaction.rollback();
		} catch(Exception e) {
			e.printStackTrace();
			try {
				bookingService.delete(travelAgentBooking);
				userTransaction.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			throw new RestServiceException(e);
		}

		log.info("created travelAgentBooking = " + travelAgentBooking.toString());
		return Response.status(Response.Status.CREATED).entity(travelAgentBooking).build();

	}

	@DELETE
	@Path("/{id:[0-9]+}")
	@ApiOperation(value = "Delete a Booking from the database")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "The booking has been successfully deleted"),
			@ApiResponse(code = 400, message = "Invalid Booking id supplied"),
			@ApiResponse(code = 404, message = "Booking with id not found"),
			@ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request") })
	public Response deleteBooking(
			@ApiParam(value = "Id of Booking to be deleted", allowableValues = "range[0, infinity]", required = true)
			@PathParam("id") long id) {

		Response.ResponseBuilder builder = null;
		TravelAgentBooking booking = bookingService.findById(id);
		if (booking == null) {
			// Verify booking exists. if not present return 404.
			throw new RestServiceException("No TravelAgentBooking with the id " + id + " was found!", Response.Status.NOT_FOUND);
		}
		log.info("TravelAgentBooking found!!!!!!!!!!!!!!!!!!!!!!!"+booking.toString());
		try {
			bookingService.delete(booking);
			builder = Response.noContent();
		} catch (Exception e) {
			// Handle generic exceptions
			throw new RestServiceException(e);
		}
		log.info("delete Booking completed. Booking : " + booking.toString());
		return builder.build();
	}

	/**
	 * <p> BookingRestService retrieve all TravelAgentBookings</p>
	 *
	 * @return javax.ws.rs.core.Response
	 */
	@GET
	@Path("/all")
	@ApiOperation(value = "Fetch all Bookings", notes = "Returns a JSON array of all stored Booking objects.")
	public Response retrieveAllBookings() {
		List<TravelAgentBooking> allBookings = new ArrayList<>();
		try {
			allBookings = bookingService.findAllBookings();
		}catch (NoResultException ne){
			ne.printStackTrace();
			log.severe("NoResultException while retrieving all TravelAgentBookings : " + ne.getMessage());
			return Response.status(Response.Status.NOT_FOUND).entity(allBookings).build();
		}
		return Response.ok(allBookings).build();
	}
}
