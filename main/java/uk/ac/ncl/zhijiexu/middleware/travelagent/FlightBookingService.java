package uk.ac.ncl.zhijiexu.middleware.travelagent;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * <p>Clientside representation of an FlightBooking object pulled from an external RESTFul API.</p>
 *
 * <p>This is the mirror opposite of a server side JAX-RS service</p>
 *
 * @author JayXu
 * @since 2019/11/21
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface FlightBookingService {

    @GET
    @Path("/flights/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    Flight getFlightById(@PathParam("id") Long id);

    @POST
    @Path("/bookings")
    @Consumes(MediaType.APPLICATION_JSON)
    FlightBookingDto createFlightBooking(FlightBookingDto flightBooking);

    @DELETE
    @Path("/bookings/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteFlightBooking(@PathParam("id") Long id);
}
