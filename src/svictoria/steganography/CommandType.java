package svictoria.steganography;

public enum CommandType {
	SIZE_FILE("-sf"), SIZE_TEXT("-st"), HIDE_FILE("-hf"), HIDE_TEXT("-ht"), EXPOSE_FILE("-ef"), EXPOSE_TEXT("-et");

	private final String text;

	private CommandType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static CommandType fromString(String text) {
		if (text != null)
		{
			for (CommandType c : CommandType.values())
			{
				if (text.equalsIgnoreCase(c.text))
				{
					return c;
				}
			}
		}
		return null;
	}
}
