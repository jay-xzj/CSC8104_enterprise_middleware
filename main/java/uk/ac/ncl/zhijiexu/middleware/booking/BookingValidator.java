package uk.ac.ncl.zhijiexu.middleware.booking;
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerService;
import uk.ac.ncl.zhijiexu.middleware.taxi.Taxi;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.*;
import java.util.logging.Logger;

/**
 * @description: Class BookingValidator to validate booking instance.
 * @author JayXu
 * @date 2019/11/06 11:21
 */
public class BookingValidator {

    @Inject
    private Validator validator;

    @Inject
    private BookingRepository crud;

    @Inject
    private CustomerService customerService;

    @Inject
    private TaxiService taxiService;

    @Inject
    private @Named("logger")
    Logger log;



    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing Booking with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If booking with the same email already exists
     */
    public void validateBooking(Booking booking) throws TaxiNotFoundException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        log.info("VALIDATING---------------------" + booking.toString());
        if (!violations.isEmpty()) {
            log.info("111111111111111111111111111111111111111111 violations.size = "+violations.size());
            Iterator<ConstraintViolation<Booking>> iterator = violations.iterator();
            while (iterator.hasNext()){
                ConstraintViolation<Booking> next = iterator.next();
                log.info("MMMMMMMMMMMMMMMMMMMMMMMMM"+next.getMessage());
            }
            log.info("111111111111111111111111111111111111111111 violations.size = "+violations.iterator());
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        // Booking taxi & date: A combination of the taxi for which a booking is made
        // and the date for which it is made should be unique
        if (bookingAlreadyExists1(booking.getTaxi(), booking.getBookingDate())) {
            throw new UniqueBookingException("Booking validation failed : Unique Booking Violation");
        }

        if(customerNotExist(booking)){
            throw new CustomerNotFoundException("Booking validation failed : The customer with given customerId doesn't exist.");
        }

        if (taxiNotExist(booking)){
            throw new TaxiNotFoundException("Booking validation failed : The taxi with given taxiId doesn't exist.");
        }
    }

    private boolean taxiNotExist(Booking booking) {
        Long taxiId = booking.getTaxi().getId();
        Taxi taxi = taxiService.findById(taxiId);
        return taxi == null;
    }

    private boolean customerNotExist(Booking booking) {
        Long id = booking.getCustomer().getId();
        //if (id!=null){
            Customer customer = customerService.findById(id);
            return customer == null;
        /*}else{
            //create booking from guestBookingRestService: customer doesn't have id
            return false;
        }*/
    }

    /***
     * <p>
     *     Booking taxi & date: A combination of the taxi for
     *      which a booking is made and the date for
     *      which it is made should be unique.
     * </p>
     *
     * @param taxi taxi to be rent
     * @param date the date when customer rents the taxi
     * @return boo taxi already has been rent or not
     */
    private boolean bookingAlreadyExists1(Taxi taxi, Date date){
        log.info("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTT: "+taxi.toString());
        boolean boo = false;
       /* String registration = taxi.getRegistration();
        System.out.println("SSSSSSSSSSSSSSSS"+registration);*/

        List<Booking> bookings = crud.findByTaxiId(taxi.getId());
        System.out.println("BBBBBBBBBBBBBBB"+bookings);

        if (bookings==null){
            return boo;
        }
        log.info("111111111111111111111");
        if(bookings.size()>0){
            log.info("bookings.size()>0 @@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            //check date
            for (Booking bk: bookings) {
                Date d1 = bk.getBookingDate();
                Calendar ca1 = Calendar.getInstance();
                ca1.setTime(d1);
                ca1.set(Calendar.HOUR_OF_DAY,0);
                ca1.set(Calendar.MINUTE,0);
                ca1.set(Calendar.SECOND,0);
                ca1.set(Calendar.MILLISECOND,0);
                Calendar ca2 = Calendar.getInstance();
                ca2.setTime(date);
                ca2.set(Calendar.HOUR_OF_DAY,0);
                ca2.set(Calendar.MINUTE,0);
                ca2.set(Calendar.SECOND,0);
                ca2.set(Calendar.MILLISECOND,0);
                log.info("CACACACACACACACA"+ca1.getTime().toString());
                log.info("CCCCCCCCCCCCCCCC"+ca2.getTime().toString());
                if (ca1.getTime().compareTo(ca2.getTime()) == 0){
                    boo = true;
                    break;
                }
            }
        }
        return boo;
    }
}
