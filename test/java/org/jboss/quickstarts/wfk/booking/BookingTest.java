package org.jboss.quickstarts.wfk.booking;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import uk.ac.ncl.zhijiexu.middleware.booking.*;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerRestService;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerService;
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
 * <p> Arquillian Test for Booking Registration </p>
 * @author JayXu
 * @date 2019/11/17 01:22
 */
@RunWith(Arquillian.class)
public class BookingTest {
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
    TaxiRestService taxiRestService;

    @Inject
    TaxiService taxiService;

    @Inject
    CustomerRestService customerRestService;

    @Inject
    CustomerService customerService;

    @Inject
    BookingRestService bookingRestService;

    @Inject
    @Named("logger")
    Logger log;

    //Set millis 1611484148085 from 2020-12-24
    private Date futureDate1 = new Date(1611484148085L);

    //Set millis 1611484148085 from 2020-12-25
    private Date futureDate2 = new Date(1611577366406L);

   /**
    * <p>
    *     BookingTest tests booking persistence with customer and taxi objects which are stored in database.
    * </p>
    */
    @Test
    @InSequence(1)
    public void testRegister(){
        Customer customer0 = createCustomerInstance("Jane Doe", "jane@mailinator.com", "07744754955");
        Customer customer = storeCustomer(customer0);
        Taxi taxi0 = createTaxiInstance("JK66AKB",6);
        Taxi taxi = storeTaxi(taxi0);
        //create a booking with a future date.
        Booking booking = createBookingInstance(customer, taxi, futureDate1);
        Response response = bookingRestService.createBooking(booking);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New booking was persisted and returned status " + response.getStatus());
    }

    /**
     * <p>
     *     BookingTest tests invalidly create a booking record.
     * </p>
     */
    @Test
    @InSequence(2)
    public void testInvalidRegister() {
        Taxi taxi = createTaxiInstanceWithId("JK66AKB",6);
        Customer customer = createCustomerInstanceWithId("Jane Doe", "jane@mailinator.com", "07744754955");
        Booking booking = createBookingInstance(customer, taxi, futureDate2);

        try {
            bookingRestService.createBooking(booking);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 1, e.getReasons().size());
            log.info("Invalid booking register attempt failed with return code " + e.getStatus());
        }
    }

    /**
     * <p>
     *     BookingTest tests invalidly create a booking record with a duplicate taxi and date.
     * </p>
     */
    @Test
    @InSequence(3)
    public void testDuplicateTaxiAndDateCombination() {
        //find a existed customer
        List<Customer> customers = customerService.findAllCustomers();
        Customer customer = customers.get(0);
        //find a existed taxi
        List<Taxi> taxis = taxiService.findAllTaxis();
        Taxi taxi = taxis.get(0);
        //create a booking with a future date.
        Booking booking = createBookingInstance(customer, taxi, futureDate1);

        /*//create the booking first time
        bookingRestService.createBooking(booking);*/

        try {
            //create the booking first time
            bookingRestService.createBooking(booking);
            fail("Expected a UniqueBookingException to be thrown");
        } catch(RestServiceException e) {
            // the status should be same with the one in class BookingRestService
            assertEquals("Unexpected response status",
                    Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexpected error. Should be UniqueBookingException",
                    e.getCause() instanceof UniqueBookingException);
            assertEquals("Unexpected response body", 1,
                    e.getReasons().size());
            log.info("Invalid booking register attempt failed with return code " + e.getStatus());
        }

    }

    /**
     * <p> BookingTest test booking creation with non-existed taxi, this method should throw @see TaxiNotFoundException </p>
     * @return void
     */
   @Test
    @InSequence(4)
    public void testTaxiNotExist() {
        //create a new taxi
        Taxi taxi = createTaxiInstanceWithId("AK66KKL",16);

        //find a existed customer
        List<Customer> customers = customerService.findAllCustomers();
        Customer customer = customers.get(0);
        Booking booking = createBookingInstance(customer, taxi, futureDate2);

        try {
            bookingRestService.createBooking(booking);
            fail("Expected a TaxiNotFoundException to be thrown");
        } catch(RestServiceException e) {
            // the status should be same with the one in class BookingRestService
            assertEquals("Unexpected response status",
                    Response.Status.BAD_REQUEST, e.getStatus());
            assertTrue("Unexpected error. Should be TaxiNotFoundException",
                    e.getCause() instanceof TaxiNotFoundException);
            assertEquals("Unexpected response body", 1,
                    e.getReasons().size());
            log.info("Invalid booking register attempt failed with return code " + e.getStatus());
        }
    }

    /**
     * <p>
     *     BookingTest tests invalidly create a booking record with a non-existed customer.
     * </p>
     */
    @Test
    @InSequence(5)
    public void testCustomerNotExist() {
        // create a new customer doesn't exist
        Customer customer = createCustomerInstanceWithId("Jane Doe", "jane@mailinator.com", "07744754955");
        //find a existed taxi
        List<Taxi> taxis = taxiService.findAllTaxis();
        Taxi taxi = taxis.get(0);
        Booking booking = createBookingInstance(customer, taxi, futureDate2);

        try {
            bookingRestService.createBooking(booking);
            fail("Expected a CustomerNotFoundException to be thrown");
        } catch(RestServiceException e) {
            // the status should be same with the one in class BookingRestService
            assertEquals("Unexpected response status",
                    Response.Status.BAD_REQUEST, e.getStatus());
            assertTrue("Unexpected error. Should be CustomerNotFoundException",
                    e.getCause() instanceof CustomerNotFoundException);
            assertEquals("Unexpected response body", 1,
                    e.getReasons().size());
            log.info("Invalid booking register attempt failed with return code " + e.getStatus());
        }
    }

    /**
     * <p> BookingTest </p>
     * @param customer, taxi, bookingDate]
     * @return org.jboss.quickstarts.wfk.booking.Booking
     */
    private Booking createBookingInstance(Customer customer, Taxi taxi, Date bookingDate ){
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setTaxi(taxi);
        booking.setBookingDate(bookingDate);
        return booking;
    }

    /**
     * <p> BookingTest createCustomerInstance method to create Customer instance to be used to test booking </p>
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
     * <p> BookingTest createCustomerInstance method to create Customer instance with id to be used to test booking </p>
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
     * <p> BookingTest persists a new customer object </p>
     * @param customer customer object
     * @return org.jboss.quickstarts.wfk.customer.Customer
     */
    private Customer storeCustomer(Customer customer){
        customerRestService.createCustomer(customer);
        return customer;
    }

    /**
     * <p> BookingTest createTaxiInstance method to create Taxi instance to be used to test booking </p>
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
