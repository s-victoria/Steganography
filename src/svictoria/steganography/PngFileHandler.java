package svictoria.steganography;

public class PngFileHandler extends ImageFileHandler {

	private static final String FORMAT_NAME = "PNG";

	@Override
	protected String getFormatName() {
		return FORMAT_NAME;
	}

}
