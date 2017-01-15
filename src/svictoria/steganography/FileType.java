package svictoria.steganography;

public enum FileType {
	MP3("mp3"), BMP("bmp"), PNG("png");

	private final String text;

	private FileType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

	public static FileType fromString(String text) {
		if (text != null)
		{
			for (FileType f : FileType.values())
			{
				if (text.equalsIgnoreCase(f.text))
				{
					return f;
				}
			}
		}
		return null;
	}
}
