package dao.exception;

public class JdbcRuntimeException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JdbcRuntimeException() {
		super();
	}

	public JdbcRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public JdbcRuntimeException(String message) {
		super(message);
	}

	public JdbcRuntimeException(Throwable cause) {
		super(cause);
	}

}
