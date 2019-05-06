package ap.apb.apbuy.markets;

public class MarketException extends Exception {

	private static final long serialVersionUID = -5394041567382725160L;

	public MarketException(MarketException.ErrorCause cause) {
		super(cause.toString());
	}
	
	public enum ErrorCause {
		NULL,NOTFOUND,MIS,SAVE,LOAD,CATNOTFOUND;
	}
	
	public ErrorCause getErrorCause() {
		return ErrorCause.valueOf(ErrorCause.class, this.getMessage());
	}
}
