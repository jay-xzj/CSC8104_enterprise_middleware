package uk.ac.ncl.zhijiexu.middleware.booking;

import javax.validation.ValidationException;

/**
 * <p> ValidationException caused if a Booking record with same taxi and date are already existed in the database </p>
 * @author JayXu
 * @date 2019/11/07 17:31
 */
public class UniqueBookingException extends ValidationException {
    private static final long serialVersionUID = 3552765368063655094L;

    public UniqueBookingException(String message){
        super(message);
    }

    public UniqueBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueBookingException(Throwable cause) {
        super(cause);
    }

}
