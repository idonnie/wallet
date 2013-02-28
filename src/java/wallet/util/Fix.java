package wallet.util;

public final class Fix {
	
	private Fix() {
		throw new UnsupportedOperationException();
	}
	
	public static void require(final boolean condition, final String message) {
		if (condition) {
			return;
		}
		throw new IllegalArgumentException(message);
	}
	
	public static void require(final boolean condition0, final boolean condition1, final String message0, final String message1) {
		if (condition0) {
			if (condition1) {
				return;
			}
			throw new IllegalArgumentException(message1);
		}
		if (condition1) {
			throw new IllegalArgumentException(message0);
		} else {
			throw new IllegalArgumentException(message0 + ", " + message1);			
		}
	}		

}
