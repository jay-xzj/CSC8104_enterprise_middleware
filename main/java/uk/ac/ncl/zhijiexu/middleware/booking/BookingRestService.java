package uk.ac.ncl.zhijiexu.middleware.booking;

import io.swagger.annotations.*;
import uk.ac.ncl.zhijiexu.middleware.util.RestServiceException;
import org.jboss.resteasy.annotations.cache.Cache;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p> Booking RESTFul APIs </p>
 *
 * @author JayXu
 * @date 2019/11/17 13:31
 */
@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/bookings", description = "Operations about bookings")
@Stateless
public class BookingRestService {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private BookingService service;

    /**
     * <p>Creates a new booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param booking The Booking object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @ApiOperation(value = "Add a new Booking to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking created successfully."),
            @ApiResponse(code = 400, message = "Invalid Booking supplied in request body"),
            @ApiResponse(code = 409, message = "Booking supplied in request body conflicts with an existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    public Response createBooking(
            @ApiParam(value = "JSON representation of Booking object to be added to the database", required = true)
                    Booking booking) {
        if (booking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;
        try {
            // Go add the new Booking.
            service.create(booking);
            // Create a "Resource Created" 201 Response and pass the booking back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(booking);

        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueBookingException ube){
            ube.printStackTrace();
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("booking", "The combination of the Taxi and date of the booking is already existed, please use a unique combination");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, ube);
        }catch (TaxiNotFoundException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("taxiId", "The taxiId does not exist");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (CustomerNotFoundException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("customerId", "The customerId does not exist");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            log.severe(e.getMessage());
            throw new RestServiceException(e);
        }

        log.info("createBooking completed. Booking = " + booking.toString());
        return builder.build();
    }

    /**
     * <p> BookingRestService retrieve all bookings</p>
     *
     * @return javax.ws.rs.core.Response
     */
    @GET
    @ApiOperation(value = "Fetch all Bookings", notes = "Returns a JSON array of all stored Booking objects.")
    public Response retrieveAllBookings() {
        List<Booking> allBookings = new ArrayList<>();
        try {
            allBookings = service.findAllBookings();
        }catch (NoResultException ne){
            ne.printStackTrace();
            log.severe("NoResultException while retrieving all bookings : " + ne.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(allBookings).build();
        }
        return Response.ok(allBookings).build();
    }

    /**
     * <p> BookingRestService delete booking record with certain id. </p>
     *
     * @param id booking id
     * @return javax.ws.rs.core.Response
     */
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

        Response.ResponseBuilder builder;

        Booking booking = service.findById(id);
        if (booking == null) {
            // Verify booking exists. if not present return 404.
            throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("booking found!!!!!!!!!!!!!!!!!!!!!!!"+booking.toString());
        try {
            service.delete(booking);
            builder = Response.noContent();
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("delete Booking completed. Booking : " + booking.toString());
        return builder.build();
    }


    /**
     * <p>
     *     Be able to retrieve a collection of all bookings made by a particular Customer, with a single request.
     * </p>
     *
     * @param customerId customerId
     * @return javax.ws.rs.core.Response
     */
    @GET
    @Cache
    @Path("customer/{customerId:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Booking by Customer id",
            notes = "Returns a JSON representation of the Booking object with the provided customer id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Booking found"),
            @ApiResponse(code = 404, message = "Booking with id not found")
    })
    public Response retrieveBookingByCustomerId(
            @ApiParam(value = "CustomerId of Booking to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("customerId")
                    long customerId) {

        List<Booking> bookings = service.findByCustomerId(customerId);
        if (bookings == null || bookings.size() == 0) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No Booking of this customerId " + customerId + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("retrieveBookingByCustomerId " + customerId + ": found Booking = " + bookings.toString());

        return Response.ok(bookings).build();
    }

    /**
     * <p>
     *     Be able to retrieve a collection of all bookings made by a particular Customer, with a single request.
     * </p>
     *
     * @param bookingId bookingId
     * @return javax.ws.rs.core.Response
     */
    @GET
    @Cache
    @Path("/{bookingId:[0-9]+}")
    @ApiOperation(
            value = "Fetch a Booking by bookingId",
            notes = "Returns a JSON representation of the Booking object with the provided customer id."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message ="Booking found"),
            @ApiResponse(code = 404, message = "Booking with id not found")
    })
    public Response retrieveBookingById(
            @ApiParam(value = "CustomerId of Booking to be fetched", allowableValues = "range[0, infinity]", required = true)
            @PathParam("bookingId")
                    long bookingId) {

        Booking booking = service.findById(bookingId);
        if (booking == null) {
            // Verify that the booking exists. Return 404, if not present.
            throw new RestServiceException("No Booking with this id " + bookingId + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("retrieveBookingById " + bookingId + ": found Booking = " + booking.toString());

        return Response.ok(booking).build();
    }

}
