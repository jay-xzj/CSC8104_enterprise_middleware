package uk.ac.ncl.zhijiexu.middleware.booking;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService} with the
 * Domain/Entity Object (see {@link Booking}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 *
 * @author JayXu
 * @date 2019/11/02 19:53
 */
public class BookingRepository {

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * <p>{@link EntityManager#persist(Object) persist(Object)} takes an entity instance, adds it to the
     * context and makes that instance managed (ie future updates to the entity will be tracked)</p>
     *
     * <p>persist(Object) will set the @GeneratedValue @Id for an object.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking create(Booking booking) throws ConstraintViolationException, ValidationException {
        //log.info("BookingRepository.create() - Creating " + booking.gettId() + " " + booking.getcId() + " " + booking.getDate());
        log.info("BookingRepository.create() - Creating " + booking.getTaxi() + " " + booking.getCustomer() + " " + booking.getBookingDate());
        // Write the booking to the database.
        em.persist(booking);
        return booking;
    }


    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Booking objects
     */
    List<Booking> findAllBookings() {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Booking object, specified by a String lastName.<p/>
     *
     * @param fullName The lastName field of the Bookings to be returned
     * @return The Bookings with the specified lastName
     */
    public List<Booking> findAllByFullName(String fullName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
        Root<Booking> root = query.from(Booking.class);
        query.select(root).where(cb.equal(root.get("fullName"), fullName));
        return em.createQuery(query).getResultList();
    }


    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     *
     * <p>{@link EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param booking The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Booking update(Booking booking) {
        //log.info("BookingRepository.update() - Updating " + booking.getcId() + " " + booking.getDate() + " " + booking.gettId());
        log.info("BookingRepository.update() - Updating " + booking.getCustomer() + " " + booking.getBookingDate() + " " + booking.getTaxi());
        // Either update the booking or add it if it can't be found.
        em.merge(booking);
        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        //log.info("BookingRepository.delete() - Deleting " + booking.getcId() + " " + booking.getDate() + " " + booking.gettId());
        log.info("BookingRepository.delete() - Deleting " + booking.getCustomer() + " " + booking.getBookingDate() + " " + booking.getTaxi());

        if (booking.getId() != null) {
            em.remove(em.merge(booking));

        } else {
            log.info("BookingRepository.delete() - No ID was found so can't Delete.");
        }

        return booking;
    }


    /*public List<Booking> findByRegistration(String registration) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_TAXI, Booking.class).setParameter("tId", tId);
        return query.getResultList();
    }*/

    /**
     * <p> BookingRepository find a booking record by taxiId </p>
     * @param tId taxi id
     * @return java.util.List<org.jboss.quickstarts.wfk.booking.Booking>
     */
    public List<Booking> findByTaxiId(Long tId) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_TAXI, Booking.class).setParameter("taxiId", tId);
        return query.getResultList();
    }

    /**
     * <p> BookingRepository find a booking record by customer id. </p>
     * @param customerId customer id
     * @return java.util.List<org.jboss.quickstarts.wfk.booking.Booking>
     */
    public List<Booking> findByCustomerId(Long customerId) {
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_CUSTOMER, Booking.class).setParameter("customerId", customerId);
        return query.getResultList();
    }

    /**
     * <p> BookingRepository find a booking record by booking id. </p>
     * @param id booking id
     * @return org.jboss.quickstarts.wfk.booking.Booking
     */
    Booking findById(Long id) {
        return em.find(Booking.class, id);
    }
}