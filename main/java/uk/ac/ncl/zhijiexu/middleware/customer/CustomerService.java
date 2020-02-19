package uk.ac.ncl.zhijiexu.middleware.customer;


import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p> Interface representing all service about customer </p>
 *
 * @author JayXu
 * @date 2019/11/02 17:33
 */
@Dependent
public class CustomerService {

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private CustomerValidator validator;

    @Inject
    private CustomerRepository crud;

    @Inject
    private EntityManager em;

    private ResteasyClient client;


    /**
     * <p> This service create a new customer</p>
     * @param customer
     * @return uk.co.ncl.coursework.customer.Customer
     * @throws
     */
   public Customer create(Customer customer) throws Exception {
        log.info("CustomerService.create() - Creating " +  customer.getName()+" "+customer.getPhoneNumber()+" " + customer.getEmail());
        //Check to make sure the data fits with the parameters in the Customer model and passes validation.
        validator.validateCustomer(customer);
        // Write the customer to the database.
        return crud.create(customer);
    }

    /**
     * <p>Deletes the provided Customer object from the application database if found there.<p/>
     *
     * @param customer The Customer object to be removed from the application database
     * @return The Customer object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Customer delete(Customer customer) throws Exception {
        log.info("delete() - Deleting " + customer.toString());
        Customer deletedCustomer = null;
        if (customer.getId() != null) {
            deletedCustomer = crud.delete(customer);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }
        return deletedCustomer;
    }

    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.<p/>
     *
     * <p>Validates the data in the provided Customer object using a CustomerValidator object.<p/>
     *
     * @param customer The Customer object to be passed as an update to the application database
     * @return The Customer object that has been successfully updated in the application database
     */
    public Customer update(Customer customer) {
        log.info("CustomerService.update() - Updating " + customer.getName());
        // Check to make sure the data fits with the parameters in the Customer model and passes validation.
        validator.validateCustomer(customer);
        // Either update the customer or add it if it can't be found.
        return crud.update(customer);
    }

    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Customer objects
     */
    public List<Customer> findAllCustomers() {
        return crud.findAllCustomers();
    }

    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    public Customer findById(Long id) {
        return crud.findById(id);
    }


    /**
     * <p> Return a list of customers found by using certain name. </p>
     *
     * @param name customer's name
     * @return java.util.List<org.jboss.quickstarts.wfk.customer.Customer>
     */
    public List<Customer> findAllByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customerRoot = criteria.from(Customer.class);
        criteria.select(customerRoot).where(cb.equal(customerRoot.get("name"), name));
        return em.createQuery(criteria).getResultList();
    }

    /**
     * <p> Return a Customer Object using email </p>
     *
     * @param email
     * @return org.jboss.quickstarts.wfk.customer.Customer
     */
    public Customer findByEmail(String email) {
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email", email);
        return query.getSingleResult();
    }
}
