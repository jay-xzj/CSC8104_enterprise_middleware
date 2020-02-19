package org.jboss.quickstarts.wfk.guestbooking;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerRestService;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerService;
import uk.ac.ncl.zhijiexu.middleware.customer.UniqueEmailException;
import uk.ac.ncl.zhijiexu.middleware.guestbooking.GuestBooking;
import uk.ac.ncl.zhijiexu.middleware.guestbooking.GuestBookingRestService;
import uk.ac.ncl.zhijiexu.middleware.taxi.Taxi;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiRestService;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiService;
import uk.ac.ncl.zhijiexu.middleware.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Customer creation functionality
 * (see {@link GuestBookingRestService#createBooking(GuestBooking)}).<p/>
 *
 * @author JayXu
 * @see GuestBookingRestService
 */
@RunWith(Arquillian.class)
public class GuestBookingTest {

    /**
     * <p>
     * Compiles an Archive using Shrinkwrap, containing those external dependencies
     * necessary to run the tests.
     * </p>
     *
     * <p>
     * Note: This code will be needed at the start of each Arquillian test, but should not
     * need to be edited, except to pass *.class values to .addClasses(...) which are
     * appropriate to the functionality you are trying to test.
     * </p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("io.swagger:swagger-jaxrs:1.5.16")
                .withTransitivity()
                .asFile();

        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackages(true, "org.jboss.quickstarts.wfk")
                .addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private GuestBookingRestService guestBookingRestService;

    @Inject
    TaxiRestService taxiRestService;

    @Inject
    TaxiService taxiService;

    @Inject
    CustomerService customerService;

    @Inject
    CustomerRestService customerRestService;

    @Inject
    @Named("logger")
    Logger log;

    //Set millis 1611484148085 from 2020-12-24
    private Date futureDate1 = new Date(1611484148085L);

    //Set millis 1611484148085 from 2020-12-25
    private Date futureDate2 = new Date(1611577366406L);

    /**
     * <p> GuestBookingTest persists a new guestBooking record </p>
     * @param
     * @return
     */
    @Test
    @InSequence(1)
    public void testRegister(){
        Customer customer = createCustomerInstance("Jane Doe", "jane@mailinator.com", "07744754955");
        //Customer customer = storeCustomer(customer0);
        Taxi taxi0 = createTaxiInstance("JK66AKB",6);
        Taxi taxi = storeTaxi(taxi0);
        //create a GuestBooking with a future date.
        GuestBooking guestBooking = createGuestBookingInstance(customer, taxi.getId(), futureDate1);
        Response response = guestBookingRestService.createBooking(guestBooking);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New booking via GuestBookingService.createBooking was persisted and returned status " + response.getStatus());
    }

    /**
     * <p> GuestBookingTest persists a new guestBooking record with invalid customer </p>
     * @param
     * @return
     */
    @Test
    @InSequence(2)
    public void testInvalidRegister() {
        //find a existed customer
        List<Customer> customers = customerService.findAllCustomers();
        Customer customer = customers.get(0);
        //find a existed taxi
        List<Taxi> taxis = taxiService.findAllTaxis();
        Taxi taxi = taxis.get(0);
        GuestBooking guestBooking = createGuestBookingInstance(customer, taxi.getId(), futureDate1);

        try {
            guestBookingRestService.createBooking(guestBooking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            // the status should be same with the one in class BookingRestService
            assertEquals("Unexpected response status",
                    Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexpected error. Should be UniqueEmailException",
                    e.getCause() instanceof UniqueEmailException);
            assertEquals("Unexpected response body", 1,
                    e.getReasons().size());
            log.info("Invalid booking register attempt failed with return code " + e.getStatus());
        }
    }

    /**
     * <p> GuestBookingTest create a new GuestBooking Object </p>
     * @param customer, taxi, bookingDate]
     * @return org.jboss.quickstarts.wfk.booking.Booking
     */
    private GuestBooking createGuestBookingInstance(Customer customer, Long taxiId, Date bookingDate ){
        GuestBooking booking = new GuestBooking();
        booking.setCustomer(customer);
        booking.setTaxiId(taxiId);
        booking.setBookingDate(bookingDate);
        return booking;
    }

    /**
     * <p> GuestBookingTest create Customer instance to be used to test booking </p>
     * @param name customer's name
     * @param email customer's email
     * @param phoneNumber customer's phone number
     * @return org.jboss.quickstarts.wfk.taxi.Taxi
     */
    private Customer createCustomerInstance(String name, String email, String phoneNumber) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

    /**
     * <p> GuestBookingTest createCustomerInstance method to create Customer instance with id to be used to test booking </p>
     * @param name customer's name
     * @param email customer's email
     * @param phoneNumber customer's phone number
     * @return org.jboss.quickstarts.wfk.taxi.Taxi
     */
    private Customer createCustomerInstanceWithId(String name, String email, String phoneNumber) {
        Customer customer = new Customer();
        customer.setId(1000L);
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

    /**
     * <p> GuestBookingTest persists a new customer object </p>
     * @param customer customer object
     * @return org.jboss.quickstarts.wfk.customer.Customer
     */
    private Customer storeCustomer(Customer customer){
        customerRestService.createCustomer(customer);
        return customer;
    }

    /**
     * <p> GuestBookingTest createTaxiInstance method to create Taxi instance to be used to test booking </p>
     * @param registration Taxi's registration
     * @param seatsNumber Taxi's seat number
     * @return org.jboss.quickstarts.wfk.taxi.Taxi
     */
    private Taxi createTaxiInstance(String registration, Integer seatsNumber) {
        Taxi taxi = new Taxi();
        taxi.setRegistration(registration);
        taxi.setSeatsNumber(seatsNumber);
        return taxi;
    }

    /**
     * <p> BookingTest createTaxiInstance method to create Taxi instance with id to be used to test booking </p>
     * @param registration Taxi's registration
     * @param seatsNumber Taxi's seat number
     * @return org.jboss.quickstarts.wfk.taxi.Taxi
     */
    private Taxi createTaxiInstanceWithId(String registration, Integer seatsNumber) {
        Taxi taxi = new Taxi();
        taxi.setId(1000L);
        taxi.setRegistration(registration);
        taxi.setSeatsNumber(seatsNumber);
        return taxi;
    }

    /**
     * <p> BookingTest persists a taxi object </p>
     * @param taxi taxi object
     * @return org.jboss.quickstarts.wfk.taxi.Taxi
     */
    private Taxi storeTaxi(Taxi taxi){
        taxiRestService.createTaxi(taxi);
        return taxi;
    }

}
