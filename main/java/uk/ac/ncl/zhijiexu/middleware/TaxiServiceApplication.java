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
package uk.ac.ncl.zhijiexu.middleware;


import io.swagger.jaxrs.config.BeanConfig;
import uk.ac.ncl.zhijiexu.middleware.booking.BookingRestService;
import uk.ac.ncl.zhijiexu.middleware.guestbooking.GuestBookingRestService;
import uk.ac.ncl.zhijiexu.middleware.customer.CustomerRestService;
import uk.ac.ncl.zhijiexu.middleware.taxi.TaxiRestService;
import uk.ac.ncl.zhijiexu.middleware.travelagent.TravelAgentBookingRestService;
import uk.ac.ncl.zhijiexu.middleware.util.JacksonConfig;
import uk.ac.ncl.zhijiexu.middleware.util.RestServiceExceptionHandler;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6 "no XML" approach to activating
 * JAX-RS.
 *
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath} annotation.
 * </p>
 */
@ApplicationPath("/api")
public class TaxiServiceApplication extends Application {

    public TaxiServiceApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("0.1.0");
        beanConfig.setSchemes(new String[]{"http"});
        // We may no longer need to change this
        // beanConfig.setHost("localhost:8080/jboss-contacts-swagger");
        beanConfig.setBasePath("/api");
        beanConfig.setTitle("JBoss Taxis APIs");
        beanConfig.setDescription("JBoss Taxis Swagger APIs");
        //Add additional RESTService containing packages here, separated by commas:
        // "org.jboss.quickstarts.wfk.contact," +
        // "org.jboss.quickstarts.wfk.other"
        beanConfig.setResourcePackage("uk.ac.ncl.zhijiexu.middleware");
        //beanConfig.setResourcePackage("org.jboss.quickstarts.wfk");
        beanConfig.setScan(true);

        //Do not edit below
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> services = new HashSet<>();

        //Add RESTful resources here as you create them
        //services.add(ContactRestService.class);
        services.add(TaxiRestService.class);
        services.add(CustomerRestService.class);
        services.add(BookingRestService.class);
        services.add(GuestBookingRestService.class);
        services.add(TravelAgentBookingRestService.class);

        //Do not edit below
        services.add(RestServiceExceptionHandler.class);
        services.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        services.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        return services;
    }

    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        singletons.add(new JacksonConfig());
        return singletons;
    }


}
