package uk.ac.ncl.zhijiexu.middleware.travelagent;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * <p> HotelService </p>
 * 
 * @author JayXu
 * @since 2019/11/21
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public interface HotelBookingService {

    @GET
    @Path("/Hotel/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    Hotel getHotelById(@PathParam("id") Long id);

    @POST
    @Path("/Booking")
    @Consumes(MediaType.APPLICATION_JSON)
    HotelBookingDto createHotelBooking(HotelBookingDto hotelBookingDto);

    @DELETE
    @Path("/Booking/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteHotelBooking(@PathParam("id") Long id);
}
