package org.jboss.quickstarts.wfk.taxi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import uk.ac.ncl.zhijiexu.middleware.util.RestServiceException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.ac.ncl.zhijiexu.middleware.taxi.Taxi;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiRestService;
import uk.ac.ncl.zhijiexu.middleware.taxi.UniqueRegistrationException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * <p>
 * A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test
 * the JAX-RS endpoint for Taxi creation functionality
 * (see {@link TaxiRestService#createTaxi(Taxi)} ).
 * </p>
 *
 * @author JayXu
 *
 * @see TaxiRestService
 */
@RunWith(Arquillian.class)
public class TaxiTest {

    /**
     * <p>
     * Compiles an Archive using Shrinkwrap, containing those external dependencies
     * necessary to run the tests.
     * </p>
     *
     * <p>
     * Note: This code will be needed at the start of each Arquillian test, but
     * should not need to be edited, except to pass *.class values to
     * .addClasses(...) which are appropriate to the functionality you are trying to
     * test.
     * </p>
     *
     * @return Micro test war to be deployed and executed.
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("io.swagger:swagger-jaxrs:1.5.15")
                .withTransitivity()
                .asFile();

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "uk.ac.ncl.enterprisemiddleware")
                .addPackages(true, "org.jboss.quickstarts.wfk").addAsLibraries(libs)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource("arquillian-ds.xml").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    TaxiRestService taxiRestService;

    @Inject
    @Named("logger")
    Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    /**
     * <p> TaxiTest persists a taxi object correctly </p>
     * @param
     * @return void
     */
    @Test
    @InSequence(1)
    public void testRegister(){
        Taxi taxi = createTaxiInstance("JK66AKB",6);
        Response response = taxiRestService.createTaxi(taxi);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New taxi was persisted and returned status " + response.getStatus());
    }

    /**
     * <p> TaxiTest invalidly persist a taxi object </p>
     * @param
     * @return void
     */
    @Test
    @InSequence(2)
    public void testInvalidRegister() {
        Taxi taxi = createTaxiInstance("", 0);

        try {
            taxiRestService.createTaxi(taxi);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status",
                    Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body",
                    2, e.getReasons().size());
            log.info("Invalid taxi register attempt failed with return code " + e.getStatus());
        }

    }

    /**
     * <p> TaxiTest persist a taxi object with duplicated registration </p>
     * @param
     * @return void
     */
    @Test
    @InSequence(3)
    public void testDuplicateRegistration() {
        // Register an initial user
        Taxi taxi = createTaxiInstance("JK66YKK", 8);
        taxiRestService.createTaxi(taxi);
        
        // Register a different user with the same registration
        Taxi anotherTaxi = createTaxiInstance("JK66YKK", 8);

        try {
            taxiRestService.createTaxi(anotherTaxi);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status",
                    Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexpected error. Should be Unique registration violation",
                    e.getCause() instanceof UniqueRegistrationException);
            assertEquals("Unexpected response body",
                    1, e.getReasons().size());
            log.info("Duplicate taxi register attempt failed with return code " + e.getStatus());
        }
    }

    /**
     * <p>A utility method to construct a {@link Taxi Taxi} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param registration The registration of the Taxi being created
     * @param seatsNumber  The seats number of the Taxi being created
     * @return The Taxi object create
     */
    private Taxi createTaxiInstance(String registration, Integer seatsNumber) {
        Taxi taxi = new Taxi();
        taxi.setRegistration(registration);
        taxi.setSeatsNumber(seatsNumber);
        return taxi;
    }

}
