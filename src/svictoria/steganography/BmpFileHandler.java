package svictoria.steganography;

public class BmpFileHandler extends ImageFileHandler {
	
	private static final String FORMAT_NAME = "BMP";

	@Override
	protected String getFormatName() {
		return FORMAT_NAME;
	}

}
