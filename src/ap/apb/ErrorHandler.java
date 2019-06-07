package ap.apb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ErrorHandler {

	public static List<String> getError(APBuyException e) {
		List<String> list = new ArrayList<>();
		if (e == null) {
			return list;
		}
		list.add("APBuy Exception: " + (e.getErrorCause() == null ? "Unknown" : e.getErrorCause().toString()));
		list.add("When occured: " + (e.getTime() == 0 ? "Unknown"
				: new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(e.getTime()))));
		list.add("Where: " + (e.getLocation() == null ? "Unknown" : e.getLocation()));
		list.add("");
		list.add("----------------------------------------------------------------------");
		list.add("APBuyException Stacktrace:");
		for (int i = 0; i < 5; i++) {
			if (i == e.getStackTrace().length) {
				break;
			}
			list.add("   " + e.getStackTrace()[i]);
		}
		if (e.getStackTrace().length >= 6) {
			list.add("   and " + (e.getStackTrace().length - 5) + " more...");
		}
		if (e.getCause() != null) {
			list.add("");
			list.add("----------------------------------------------------------------------");
			list.add("Caused by Exception: " + e.getCause().getClass().getName());
			list.add("Message: " + e.getCause().getMessage());
			list.add("");
			list.add("Stacktrace:");
			for (int i = 0; i < 25; i++) {
				if (i == e.getCause().getStackTrace().length) {
					break;
				}
				list.add("   " + e.getCause().getStackTrace()[i]);
			}
			if (e.getStackTrace().length >= 26) {
				list.add("   and " + (e.getCause().getStackTrace().length - 25) + " more...");
			}
			
			if (e.getCause().getCause() != null) {
				list.add("");
				list.add("Caused by: " + e.getCause().getCause().getClass().getName());
				list.add("Message: "
						+ (e.getCause().getCause().getMessage() != null ? e.getCause().getCause().getMessage() : "Unknown"));
				list.add("");
				for (int i = 0; i < 25; i++) {
					if (i == e.getCause().getCause().getStackTrace().length) {
						break;
					}
					list.add("   " + e.getCause().getCause().getStackTrace()[i]);
				}
				if (e.getCause().getCause().getStackTrace().length >= 26) {
					list.add("   and " + (e.getCause().getCause().getStackTrace().length - 25) + " more...");
				}
			}
		}
		return list;
	}

}
