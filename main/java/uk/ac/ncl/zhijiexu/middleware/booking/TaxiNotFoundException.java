package uk.ac.ncl.zhijiexu.middleware.booking;

import javax.validation.ValidationException;

/**
 * <p> Throw this exception when taxi doesn't exist. </p>
 * @author JayXu
 * @date 2019/11/19 21:45
 */
public class TaxiNotFoundException  extends ValidationException {
    private static final long serialVersionUID = 728388511064997803L;
    public TaxiNotFoundException(String message) {
        super(message);
    }

    public TaxiNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaxiNotFoundException(Throwable cause) {
        super(cause);
    }
}
