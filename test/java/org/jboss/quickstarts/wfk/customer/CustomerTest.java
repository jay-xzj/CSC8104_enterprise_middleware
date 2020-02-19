/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.quickstarts.wfk.customer;

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
import uk.ac.ncl.zhijiexu.middleware.customer.Customer;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerRestService;
import uk.ac.ncl.zhijiexu.middleware.customer.UniqueEmailException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * <p>A suite of tests, run with {@link org.jboss.arquillian Arquillian} to test the JAX-RS endpoint for
 * Customer creation functionality
 * (see {@link CustomerRestService#createCustomer(Customer) createCustomer(Customer)}).<p/>
 *
 * @author JayXu
 * @see CustomerRestService
 */
@RunWith(Arquillian.class)
public class CustomerTest {

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
    CustomerRestService customerRestService;

    @Inject
    @Named("logger") Logger log;

    //Set millis 498484800000 from 1985-10-10T12:00:00.000Z
    private Date date = new Date(498484800000L);

    /**
     * <p> CustomerTest tests persisting a customer object </p>
     *
     * @return void
     */
    @Test
    @InSequence(1)
    public void testRegister(){
        Customer customer = createCustomerInstance("Jack Doe", "jack@mailinator.com", "07744754955");
        Response response = customerRestService.createCustomer(customer);

        assertEquals("Unexpected response status", 201, response.getStatus());
        log.info(" New customer was persisted and returned status " + response.getStatus());
    }

    /**
     * <p> CustomerTest tests invalidly persisting a customer object </p>
     *
     * @return void
     */
    @Test
    @InSequence(2)
    public void testInvalidRegister() {
        Customer customer = createCustomerInstance("", "", "");

        try {
            customerRestService.createCustomer(customer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status", Response.Status.BAD_REQUEST, e.getStatus());
            assertEquals("Unexpected response body", 3, e.getReasons().size());
            log.info("Invalid customer register attempt failed with return code " + e.getStatus());
        }

    }

    /**
     * <p> CustomerTest tests persisting a customer object with duplicate email </p>
     *
     * @return void
     */
    @Test
    @InSequence(3)
    public void testDuplicateEmail() {
        // Register an initial user
        Customer customer = createCustomerInstance("Jane Doe", "jane@mailinator.com", "07744754955");
        customerRestService.createCustomer(customer);

        // Register a different user with the same email
        Customer anotherCustomer = createCustomerInstance("John Doe", "jane@mailinator.com", "07744754955");

        try {
            customerRestService.createCustomer(anotherCustomer);
            fail("Expected a RestServiceException to be thrown");
        } catch(RestServiceException e) {
            assertEquals("Unexpected response status",
                    Response.Status.CONFLICT, e.getStatus());
            assertTrue("Unexpected error. Should be Unique email violation",
                    e.getCause() instanceof UniqueEmailException);
            assertEquals("Unexpected response body", 1,
                    e.getReasons().size());
            log.info("Duplicate customer register attempt failed with return code " + e.getStatus());
        }

    }

    /**
     * <p>A utility method to construct a {@link Customer Customer} object for use in
     * testing. This object is not persisted.</p>
     *
     * @param name The name of the Customer being created
     * @param email     The email address of the Customer being created
     * @param phoneNumber     The phone number of the Customer being created
     * @return The Customer object create
     */
    private Customer createCustomerInstance(String name, String email, String phoneNumber) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        return customer;
    }

}
