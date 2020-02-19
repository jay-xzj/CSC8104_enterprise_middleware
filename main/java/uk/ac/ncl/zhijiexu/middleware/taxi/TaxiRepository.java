package uk.ac.ncl.zhijiexu.middleware.taxi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link TaxiService} with the
 * Domain/Entity Object (see {@link Taxi}).<p/>
 *
 * <p>There are no access modifiers on the methods making them 'package' scope.  They should only be accessed by a
 * Service/Control object.<p/>
 *
 * @author JayXu
 * @date 2019/11/04 10:18
 */
public class TaxiRepository {
    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p> Persist a taxi Object  </p>
     * @param taxi taxi object
     * @return org.jboss.quickstarts.wfk.taxi.Taxi
     */
    public Taxi create(Taxi taxi) {
        log.info("CustomerRepository.create() - Creating " + taxi.getRegistration() + " " + taxi.getSeatsNumber());
        // Write the taxi to the database.
        em.persist(taxi);
        return taxi;
    }

    /**
     * <p>Deletes the provided Taxi object from the application database if found there</p>
     *
     * @param taxi The Taxi object to be removed from the application database
     * @return The Taxi object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Taxi delete(Taxi taxi) throws Exception {
        log.info("TaxiRepository.delete() - Deleting " + taxi.getRegistration() + " " + taxi.getSeatsNumber());
        if (taxi.getId() != null) {
            em.remove(em.merge(taxi));
        } else {
            log.info("TaxiRepository.delete() - No ID was found so can't Delete.");
        }
        return taxi;
    }

    /**
     * <p>Updates an existing Taxi object in the application database with the provided Taxi object.</p>
     *
     * <p>{@link EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param taxi The Taxi object to be merged with an existing Taxi
     * @return The Taxi that has been merged
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    public Taxi update(Taxi taxi) {
        log.info("TaxiRepository.update() - Updating " + taxi.getRegistration() + " " + taxi.getSeatsNumber());
        // Either update the taxi or add it if it can't be found.
        em.merge(taxi);
        return taxi;
    }

    /**
     * <p>Returns a List of all persisted {@link Taxi} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Taxi objects
     */
    public List<Taxi> findAllTaxis() {
        TypedQuery<Taxi> query = em.createNamedQuery(Taxi.FIND_ALL, Taxi.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Taxi object, specified by a Integer id.<p/>
     *
     * @param id The id field of the Taxi to be returned
     * @return The Taxi with the specified id
     */
    public Taxi findById(Long id) {
        return em.find(Taxi.class, id);
    }

    /**
     * <p>Returns a single Taxi object, specified by a String registration.</p>
     *
     * <p>If there is more than one Taxi with the specified registration, only the first encountered will be returned.<p/>
     *
     * @param registration The registration field of the Taxi to be returned
     * @return The first Taxi with the specified registration
     */
    public Taxi findByRegistration(String registration) {
        TypedQuery<Taxi> query = em
                .createNamedQuery(Taxi.FIND_BY_REGISTRATION, Taxi.class)
                .setParameter("registration", registration);
        return query.getSingleResult();
    }

    /**
     * <p>Returns a list of Taxi objects, specified by a seats.<p/>
     *
     * @param seatsNumber The seatsNumber field of the Taxis to be returned
     * @return The Taxis with the specified seatsNumber
     */
    public List<Taxi> findAllBySeatsNumber(Integer seatsNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Taxi> criteria = cb.createQuery(Taxi.class);
        Root<Taxi> taxi = criteria.from(Taxi.class);
        criteria.select(taxi).where(cb.equal(taxi.get("seatsNumber"), seatsNumber));
        return em.createQuery(criteria).getResultList();
    }

}
