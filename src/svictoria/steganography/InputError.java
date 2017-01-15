package svictoria.steganography;

public enum InputError {
	ARGUMENTS_OVERLOAD("Wrong amount of arguments"), ILLEGAL_COMMAND("Illegal command entered"), ILLEGAL_SOURCE(
			"Source file does not exist"), ILLEGAL_DESTINATION("Destination file does not exist"), SOURCE_TOO_LARGE(
					"Source data is too large"), SOURCE_NOT_TEXT("Source file is not a text file");

	private final String text;

	private InputError(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}