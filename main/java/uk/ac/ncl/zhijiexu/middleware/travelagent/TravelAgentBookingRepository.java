package uk.ac.ncl.zhijiexu.middleware.travelagent;

import uk.ac.ncl.zhijiexu.middleware.booking.Booking;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>
 *     TravelAgentBookingRepository provides methods to manipulate crud of travelAgentBooking.
 * </p>
 *
 * @author JayXu
 * @since 2019/11/20
 */
public class TravelAgentBookingRepository {
	@Inject
	private @Named("logger") Logger log;

	@Inject
	private EntityManager em;

	/**
	 * <p>
	 * Returns a List of all persisted {@link Booking} objects, sorted
	 * alphabetically by last name.
	 * </p>
	 *
	 * @return List of Booking objects
	 */
	List<TravelAgentBooking> findAllOrderedById() {
		TypedQuery<TravelAgentBooking> query = em.createNamedQuery(TravelAgentBooking.FIND_ALL,
				TravelAgentBooking.class);
		return query.getResultList();
	}

	/**
	 * <p>
	 * Returns a single Booking object, specified by a Long id.
	 * </p>
	 *
	 * @param id The id field of the Booking to be returned
	 * @return The Booking with the specified id
	 */
	TravelAgentBooking findById(Long id) {
		return em.find(TravelAgentBooking.class, id);
	}

	/**
	 * <p>
	 * Persists the provided TravelAgentBooking object to the application database using the
	 * EntityManager.
	 * </p>
	 *
	 * @param booking The TravelAgentBooking object to be persisted
	 * @return booking The TravelAgentBooking object that has been persisted
	 * @throws ConstraintViolationException, ValidationException, Exception
	 */
	TravelAgentBooking create(TravelAgentBooking booking) throws ConstraintViolationException, ValidationException, Exception {
		log.info("TravelAgentBookingRepository.create() - Creating " + booking.getId());
		// Persist TravelAgentBooking to the database.
		em.persist(booking);

		return booking;
	}

	/**
	 * <p>
	 * Deletes the provided Booking object from the application database if found
	 * there
	 * </p>
	 *
	 * @param booking The Booking object to be removed from the application database
	 * @return The Booking object that has been successfully removed from the application database; or null
	 * @throws Exception
	 */
	TravelAgentBooking delete(TravelAgentBooking booking) throws Exception {
		log.info("BookingRepository.delete() - Deleting " + booking.getId());

		if (booking.getId() != null) {
			em.remove(em.merge(booking));

		} else {
			log.info("BookingRepository.delete() - No ID was found so can't Delete.");
		}

		return booking;
	}

	public List<TravelAgentBooking> findAllAgentBookings() {
		TypedQuery<TravelAgentBooking> query = em.createNamedQuery(TravelAgentBooking.FIND_ALL, TravelAgentBooking.class);
		return query.getResultList();
	}

}
