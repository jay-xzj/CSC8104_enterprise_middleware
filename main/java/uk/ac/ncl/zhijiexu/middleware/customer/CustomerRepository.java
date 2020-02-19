package uk.ac.ncl.zhijiexu.middleware.customer;
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
 * <p> CustomerRepository provide crud method to manipulate Customer record data in database. </p>
 *
 * @author JayXu
 * @date 2019/11/04 09:57
 */
public class CustomerRepository {

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private EntityManager em;

    /**
     * <p>
     * Persists the provided Customer object to the application database using the
     * EntityManager.
     * </p>
     *
     * <p>
     * {@link javax.persistence.EntityManager#persist(Object) persist(Object)} takes
     * an entity instance, adds it to the context and makes that instance managed
     * (ie future updates to the entity will be tracked)
     * </p>
     *
     * <p>
     * persist(Object) will set the @GeneratedValue @Id for an object.
     * </p>
     *
     * @param customer The Customer object to be persisted
     * @return The Customer object that has been persisted
     * @throws ConstraintViolationException,
     *             ValidationException, Exception
     */
    Customer create(Customer customer) throws ConstraintViolationException, ValidationException, Exception {
        log.info("CustomerRepository.create() - Creating " + customer.getName());

        // Write the customer to the database.
        em.persist(customer);

        return customer;
    }

    /**
     * <p>Deletes the provided Taxi object from the application database if found there</p>
     *
     * @param customer The Taxi object to be removed from the application database
     * @return The Taxi object that has been successfully removed from the application database; or null
     */
    public Customer delete(Customer customer) {
        log.info("CustomerRepository.delete() - Deleting " + customer.getName()+" "+customer.getPhoneNumber()+" "+customer.getEmail());
        if (customer.getId() != null) {
            em.remove(em.merge(customer));
        } else {
            log.info("CustomerRepository.delete() - No ID was found so can't Delete.");
        }
        return customer;
    }


    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.</p>
     *
     * <p>{@link javax.persistence.EntityManager#merge(Object) merge(Object)} creates a new instance of your entity,
     * copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be
     * managed (any changes you make will not be part of the transaction - unless you call merge again).</p>
     *
     * <p>merge(Object) however must have an object with the @Id already generated.</p>
     *
     * @param customer The Customer object to be merged with an existing Customer
     * @return customer The Customer that has been merged
     */
    public Customer update(Customer customer) {
        log.info("CustomerRepository.update() - Updating " + customer.getName()+" "+customer.getPhoneNumber()+" "+customer.getEmail());
        // Either update the customer or add it if it can't be found.
        em.merge(customer);
        return customer;
    }

    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by last name.</p>
     *
     * @return List of Booking objects
     */
    public List<Customer> findAllCustomers() {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        return query.getResultList();
    }

    /**
     * <p>Returns a single Customer object, specified by a String email.</p>
     *
     * <p>If there is more than one Customer with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Customer to be returned
     * @return The first Customer with the specified email
     */
    public Customer findByEmail(String email) {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email", email);
        return query.getSingleResult();
    }


    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    public Customer findById(Long id) {
        return em.find(Customer.class, id);
    }
}
