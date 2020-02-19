package uk.ac.ncl.zhijiexu.middleware.booking;

import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p> Booking Service class contains all methods to crud booking record </p>
 *
 * @author JayXu
 * @date 2019/11/02 18:22
 */
@Dependent
public class BookingService {

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private BookingRepository crud;

    /*@Inject
    private CustomerRepository ccrud;

    @Inject
    private TaxiRepository tcrud;*/

    @Inject
    private BookingValidator validator;

    private ResteasyClient client;

    /***
     * @Description: BookingService
     * @param booking
     * @return org.jboss.quickstarts.wfk.booking.Booking
     * @throws
     */
    public Booking create(Booking booking){
        log.info("BookingService creating before!");
        validator.validateBooking(booking);
        log.info("BookingService creating after!");
        return crud.create(booking);
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Booking delete(Booking booking) throws Exception {
        log.info("delete() - Deleting " + booking.toString());

        Booking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedBooking;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.<p/>
     *
     * <p>Validates the data in the provided Booking object using a BookingValidator object.<p/>
     *
     * @param booking The Booking object to be passed as an update to the application database
     * @return The Booking object that has been successfully updated in the application database
     */
    public Booking update(Booking booking) {
        //log.info("BookingService.update() - Updating " + booking.getcId() + " " + booking.getDate() + " " + booking.gettId());
        log.info("BookingService.update() - Updating " + booking.getCustomer() + " " + booking.getBookingDate() + " " + booking.getTaxi());
        // validation
        validator.validateBooking(booking);
        // Either update the booking or add it if it can't be found.
        return crud.update(booking);
    }

    /***
     * <p> BookingService find a booking record with certain id </p>
     * @param id booking id
     * @return org.jboss.quickstarts.wfk.booking.Booking
     */
    Booking findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>
     * Returns a single Booking object, specified by a Long CustomerId.
     * </p>
     *
     * @param customerId
     *            The id field of the Booking to be returned
     * @return The Booking with the specified CustomerId
     */
    public List<Booking> findByCustomerId(Long customerId) {
        return crud.findByCustomerId(customerId);
    }

    public List<Booking> findAllBookings(){
        return crud.findAllBookings();
    }

    /***
     * Be able to retrieve a collection of all bookings made by a particular Customer, with a single request.</p>
     * @param customer who wants to query the
     * @return java.util.List<org.jboss.quickstarts.wfk.booking.Booking> class booking
     * @throws
     */
    public List<Booking> list(Customer customer){
        return crud.findAllByFullName(customer.getName());
    }
}
