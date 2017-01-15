package svictoria.steganography;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public abstract class FileHandler {

	protected static final int	RESERVED_FILE_SIZE		= 32;
	protected static final int	RESERVED_FILE_EXTENSION	= 40;

	private File				mWorkingFile;

	protected int getReservedFileData() {
		return RESERVED_FILE_EXTENSION + RESERVED_FILE_SIZE;
	}

	public void setFile(File file) {
		mWorkingFile = file;
		loadFile(mWorkingFile);
	}

	protected abstract void loadFile(File file);

	public abstract int getFileSize();

	public abstract int getTextSize();

	public String hideFile(File src) throws Exception {
		File destFile = null;
		if ((src.length() * 8) <= getFileSize())
		{
			try
			{
				String fileType = "";
				try
				{
					fileType = src.getName().split("[.]")[1];
				}
				catch (Exception e)
				{

				}

				while (fileType.length() < (RESERVED_FILE_EXTENSION / 8))
				{
					fileType += " ";
				}

				byte[] bytes = Files.readAllBytes(src.toPath());

				bytes = ByteBuffer.allocate((int) (src.length() + getReservedFileData() / 8)).put(fileType.getBytes(StandardCharsets.US_ASCII))
						.putInt((int) src.length()).put(bytes).array();

				try
				{
					destFile = getDestinationFile();
					putBytes(bytes, destFile);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			throw new Exception(InputError.SOURCE_TOO_LARGE.toString());
		}

		return destFile.getAbsolutePath();
	}

	public String hideText(String text) throws Exception {
		File destFile = null;
		if ((text.length()) <= getTextSize() / 8)
		{

			byte[] bytes = null;
			if ((text.length()) < getTextSize() / 8)
			{
				text += " ";
				bytes = text.getBytes(StandardCharsets.US_ASCII);
				bytes[bytes.length - 1] = 0;
			}
			else
			{
				bytes = text.getBytes(StandardCharsets.US_ASCII);
			}

			try
			{
				destFile = getDestinationFile();
				putBytes(bytes, destFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			throw new Exception(InputError.SOURCE_TOO_LARGE.toString());
		}

		return destFile.getAbsolutePath();
	}

	public abstract String exposeFile();

	public abstract String exposeText();

	protected File getDestinationFile() {
		String name = "hidden_" + mWorkingFile.getName();
		File destination = new File(new File("").getAbsolutePath() + File.separator + name);

		return destination;
	}

	protected File getExposedFile(String type) {
		String name = "exposed." + type;
		File destination = new File(new File("").getAbsolutePath() + File.separator + name);

		return destination;
	}

	protected abstract void putBytes(byte[] bytes, File destination) throws IOException;

}
