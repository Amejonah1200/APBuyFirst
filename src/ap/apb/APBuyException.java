package ap.apb;

public class APBuyException extends Exception {
	private static final long serialVersionUID = -5394041567382725160L;

	private ErrorCause errorCause;
	private String message;
	private String location;
	private long time;

	public APBuyException(ErrorCause cause, Exception exception, String message, String location) {
		this.errorCause = cause;
		this.initCause(exception);
		this.message = message;
		this.time = System.currentTimeMillis();
		this.location = location;
	}
	
	public ErrorCause getErrorCause() {
		return this.errorCause;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	public enum ErrorCause {
		NPE, NOTFOUND_MARKET, NOTFOUND_CAT, NOTFOUND_MIS, SQL, APBE, UNKNOWN;
	}

}
