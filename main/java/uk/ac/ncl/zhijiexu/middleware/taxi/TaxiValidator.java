package uk.ac.ncl.zhijiexu.middleware.taxi;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p> Validator class fo taxi </p>
 *
 * @author JayXu
 * @date 2019/11/07 10:43
 */
public class TaxiValidator {


    @Inject
    private Validator validator;

    @Inject
    private @Named("logger")
    Logger log;

    @Inject
    private TaxiRepository crud;

    /***
     * <p>
     * Validates the given Customer object and throws validation exceptions based on the type of error.
     * If the error is standard bean validation errors then it will throw a ConstraintValidationException
     * with the set of the constraints violated.
     * </p>
     *
     * <p>
     * Validates the given Customer object having a registration number already existed in the database, then
     * it will through an UniqueRegistrationException.
     * </p>
     *
     * @param taxi Taxi Object to be validated
     */
    public void validateTaxi(Taxi taxi) {
        // Create a bean validator and check for issues.

        log.info("Before constraint violation");
        Set<ConstraintViolation<Taxi>> violations = validator.validate(taxi);
        log.info("After constraint violation");
        if (!violations.isEmpty()) {
            log.info("Inside violations! empty");
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }


        // Check whether the taxi's registration already exists
        if (taxiRegistrationExists(taxi)) {
            throw new UniqueRegistrationException("Taxi's registration exists in system");
        }
    }

    /**
     * <p>
     * Checks if a taxi number already exists.
     * </p>
     *
     * @param taxi
     * @return boolean which represents whether a taxi number already exists
     */
    private boolean taxiRegistrationExists(Taxi taxi) {
        log.info("find by registration");
        try {
            log.severe("+++++++++++++++++++++++++++");
            Taxi findTaxi = crud.findByRegistration(taxi.getRegistration());
            log.severe("+++++++++++++++++++++++++++");
            log.info("after find by registration");
            return findTaxi != null;
        } catch (NoResultException e) {
            log.info(String.format("NoResultException = %s", e));
        }
        return false;
    }
}
