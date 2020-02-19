package uk.ac.ncl.zhijiexu.middleware.taxi;

import javax.validation.ValidationException;

/**
 * <P> Throw this exception when taxi doesn't have a unique registration. </P>
 *
 * @author JayXu
 * @date 2019/11/11 12:57
 */
public class UniqueRegistrationException extends ValidationException {

    private static final long serialVersionUID = 7815411145345524221L;

    public UniqueRegistrationException(String message) {
        super(message);
    }

    public UniqueRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueRegistrationException(Throwable cause) {
        super(cause);
    }

}
