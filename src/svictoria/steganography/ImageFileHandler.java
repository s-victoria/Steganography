package svictoria.steganography;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

public abstract class ImageFileHandler extends FileHandler {

	private static final int	MASK_ONE	= 0x00000001;
	private static final int	MASK_ZERO	= 0xfffffffe;

	private static final byte	MASK_ONE_R	= (byte) 0x80;
	private static final byte	MASK_ZERO_R	= (byte) 0x7f;

	private int[][]				mPixels;
	private BufferedImage		mImage;

	private int					mWidth;
	private int					mHeight;

	@Override
	public void loadFile(File file) {
		try
		{
			mImage = ImageIO.read(file);
			if (mImage != null)
			{
				mWidth = mImage.getWidth();
				mHeight = mImage.getHeight();

				mPixels = new int[mWidth][mHeight];

				for (int x = 0; x < mWidth; x++)
				{
					for (int y = 0; y < mHeight; y++)
					{
						mPixels[x][y] = mImage.getRGB(x, y);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int getFileSize() {
		int fileSize = 0;

		if (mPixels != null)
		{
			fileSize = mWidth * mHeight - getReservedFileData();
		}

		return fileSize;
	}

	@Override
	public int getTextSize() {
		int textSize = 0;

		if (mPixels != null)
		{
			textSize = mWidth * mHeight;
		}

		return textSize;
	}

	@Override
	protected void putBytes(byte[] bytes, File destination) throws IOException {
		if (mPixels != null)
		{
			int numOfBytes = bytes.length;
			int place = 0;

			for (int i = 0; i < numOfBytes; i++)
			{
				byte currentByte = bytes[i];
				Logger.println("byte * " + (int) (currentByte));

				for (int j = 0; j < 8; j++)
				{
					insertBit(mImage, currentByte % 2 != 0, place++);
					currentByte = (byte) (currentByte >> 1);
				}

				Logger.println("------------------------");
			}

			try
			{
				ImageIO.write(mImage, getFormatName(), destination);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public String exposeFile() {
		int place = 0;

		String fileType = "";
		for (int i = 0; i < (RESERVED_FILE_EXTENSION / 8); i++)
		{
			byte currentByte = getByte(mImage, place);
			place += 8;
			if (currentByte != ' ')
			{
				fileType += (char) currentByte;
			}
		}

		byte[] sizeBytes = new byte[RESERVED_FILE_SIZE / 8];
		for (int i = 0; i < sizeBytes.length; i++)
		{
			sizeBytes[i] = getByte(mImage, place);
			place += 8;
		}
		int fileSize = ByteBuffer.wrap(sizeBytes).getInt();

		byte[] fileBytes = new byte[fileSize];
		for (int i = 0; i < fileBytes.length; i++)
		{
			fileBytes[i] = getByte(mImage, place);
			place += 8;
		}

		Path filePath = getExposedFile(fileType).toPath();
		try
		{
			Files.write(filePath, fileBytes);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return filePath.toAbsolutePath().toString();
	}

	@Override
	public String exposeText() {
		String text = "";

		int size = mWidth * mHeight;
		int place = 0;
		while ((size - place) >= 8)
		{
			byte currentByte = getByte(mImage, place);
			place += 8;
			if (currentByte == '\0')
			{
				break;
			}

			text += (char) currentByte;
			Logger.print((char) currentByte);
		}

		return text;
	}

	protected abstract String getFormatName();

	private void insertBit(BufferedImage image, boolean b, int place) {
		int y = place / mWidth;
		int x = place % mWidth;

		int pixel = (b) ? mPixels[x][y] | MASK_ONE : mPixels[x][y] & MASK_ZERO;
		// int pixel = mPixels[x][y];
		image.setRGB(x, y, pixel);

		Logger.print("xy " + x + ":" + y + " * ");
		Logger.printBit(b);
		Logger.print(" * from " + mPixels[x][y] + " to " + pixel);
		Logger.println();
	}

	private boolean getBit(BufferedImage image, int place) {
		int y = place / mWidth;
		int x = place % mWidth;

		if (x >= mWidth || y >= mHeight)
		{
			return false;
		}
		else
		{
			return mPixels[x][y] % 2 != 0;
		}
	}

	private byte getByte(BufferedImage image, int place) {
		byte currentByte = 0;
		for (int i = 0; i < 8; i++)
		{
			currentByte = (byte) (currentByte >> 1);
			currentByte = (byte) ((getBit(image, place++)) ? currentByte | MASK_ONE_R : currentByte & MASK_ZERO_R);
		}

		return currentByte;
	}

}
