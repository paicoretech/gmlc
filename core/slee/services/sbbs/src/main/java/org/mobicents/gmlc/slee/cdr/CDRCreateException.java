package org.mobicents.gmlc.slee.cdr;

/**
 *
 * @author <a href="mailto:bbaranow@redhat.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:fernando.mendioroz@gmail.com"> Fernando Mendioroz </a>
 */
public class CDRCreateException extends RuntimeException {

  public CDRCreateException() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message   String containing the exception message
   */
  public CDRCreateException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause   Throable containing the exception cause
   */
  public CDRCreateException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message   String containing the exception message
   * @param cause     String containing the exception caus
   */
  public CDRCreateException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message             String containing the exception message
   * @param cause               Throable containing the exception cause
   * @param enableSuppression   True if suppression shall be enabled
   * @param writableStackTrace  True if writable stack trace shall be present
   */
  public CDRCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    // TODO Auto-generated constructor stub
  }

}
