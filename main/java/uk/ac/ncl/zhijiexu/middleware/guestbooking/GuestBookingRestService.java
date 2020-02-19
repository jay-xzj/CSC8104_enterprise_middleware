package uk.ac.ncl.zhijiexu.middleware.guestbooking;

import com.google.common.base.Strings;
import io.swagger.annotations.*;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerService;
import uk.ac.ncl.zhijiexu.middleware.customer.UniqueEmailException;
import uk.ac.ncl.zhijiexu.middleware.taxi.Taxi;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiService;
import uk.ac.ncl.zhijiexu.middleware.util.RestServiceException;
import uk.ac.ncl.zhijiexu.middleware.booking.Booking;
import uk.ac.ncl.zhijiexu.middleware.booking.BookingService;
import uk.ac.ncl.zhijiexu.middleware.booking.UniqueBookingException;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.*;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * <p> POJO representing guest booking objects. </p>
 * 
 * @author JayXu
 * @date 2019/11/02 17:45
 */
@TransactionManagement(value = javax.ejb.TransactionManagementType.BEAN)//This specifies that you wish to manually manage transaction boundaries inside the class.
@Path("/guestBookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "/guestBookings", description = "Operations about guest booking")
@Stateless
public class GuestBookingRestService {

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private CustomerService customerService;

    @Inject
    private TaxiService taxiService;

    @Inject
    private BookingService bookingService;

    @Inject
    private UserTransaction userTransaction;

    /***
     * <P>This method should then use the CustomerService and BookingService classes to persist the appropriate fields of the GuestBooking object
     * inside a UserTransaction and return a Response containing the Booking, and a status of 201 if successful.
     * If either Entity should fail to be persisted, rollback the transaction with an appropriate error message.</P>
     *
     * @param guestBooking The booking object,
     * @return A Response containing booking
     */
    @ApiOperation(value = "Add a new booking record to the database")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Booking created successfully."),
            @ApiResponse(code = 400, message = "Invalid GuestBooking supplied in request body"),
            @ApiResponse(code = 409, message = "GuestBooking supplied in request body conflicts with an existing Booking"),
            @ApiResponse(code = 500, message = "An unexpected error occurred whilst processing the request")
    })
    @POST
    public Response createBooking(
            @ApiParam(value = "JSON representation of GuestBooking object to be added to the database",required = true)
                    GuestBooking guestBooking){

        if (guestBooking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Booking booking = null;
        try {
            userTransaction.begin();
            //Not be able to create a Customer/Taxi/Booking record with incomplete or invalid information.
            //If customer's name doesn't exist in the database, then create a new customer
            Customer customer = guestBooking.getCustomer();
            log.info("-----------------guestBooking's Customer---------------"+customer);
            //If can find the customer by id, and find customer equals this customer, set the booking's customer
            //directly.
            Long id = customer.getId();
            Customer createdCustomer = new Customer();
            try {
                if (id!=null){
                    Customer findOne = customerService.findById(id);
                    log.info("+++++++findOne+++++++"+findOne);
                    createdCustomer.setEmail(customer.getEmail());
                    createdCustomer.setName(customer.getName());
                    createdCustomer.setPhoneNumber(customer.getPhoneNumber());
                }else{
                    createdCustomer = customer;
                }
            }catch (NotFoundException ne){
                log.severe(String.format("NotFoundException-Taxi not found : %s", ne.getMessage()));
            }
            log.info("~~~~~~~~~~~~~~~~"+createdCustomer.toString()+"~~~~~~~~~~~~~~~~");
            customerService.create(createdCustomer);

            /*Booking taxi & date: A combination of the taxi for which a booking
            is made and the date for which it is made should be unique */

            //create a Booking record, with a Customer id, a Taxi id & a future date.
            Long taxiId = guestBooking.getTaxiId();
            //use taxiId to get Taxi object to validate
            if (Strings.isNullOrEmpty(taxiId+"")){
                throw new IllegalArgumentException("taxiId is null or blank");
            }
            Taxi taxi = null;
            try {
                taxi = taxiService.findById(guestBooking.getTaxiId());
            }catch (NotFoundException ne){
                log.severe(String.format("NotFoundException-Taxi not found : %s", ne.getMessage()));
            }

            Date bookingDate = guestBooking.getBookingDate();
            assert bookingDate != null;
            assert taxi != null;
            booking = new Booking();
            booking.setTaxi(taxi);
            booking.setCustomer(createdCustomer);
            booking.setBookingDate(bookingDate);
            log.info("BBBBBBBBBBBBB"+booking.toString());
            bookingService.create(booking);

            userTransaction.commit();
        } catch(UniqueEmailException uee){
            uee.printStackTrace();
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Customer's email is already used, please use a unique email");
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
            throw new RestServiceException("Conflict", responseObj, Response.Status.CONFLICT, uee);
        } catch (UniqueBookingException ube){
            ube.printStackTrace();
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("booking", "The combination of the Taxi and date of the booking is already existed, please use a unique combination");
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
            throw new RestServiceException("Conflict", responseObj, Response.Status.CONFLICT, ube);
        }catch (ConstraintViolationException ce){
            ce.printStackTrace();
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        }catch (ValidationException ve){
            ve.printStackTrace();
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("validation", "validation failed");
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ve);
        } catch(Exception e) {
            e.printStackTrace();
            try {
                userTransaction.rollback();
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
            throw new RestServiceException(e);
        }
        return Response.status(Response.Status.CREATED).entity(booking).build();
        //return Response.ok(booking).build();
    }

}
