package uk.ac.ncl.zhijiexu.middleware.booking;

import javax.validation.ValidationException;

/**
 * <p> Create booking with not found customer </p>
 * @author JayXu
 * @date 2019/11/19 18:04
 */
public class CustomerNotFoundException extends ValidationException {

    private static final long serialVersionUID = -5363058315992561837L;

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerNotFoundException(Throwable cause) {
        super(cause);
    }

}
