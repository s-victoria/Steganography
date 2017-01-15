package svictoria.steganography;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class MP3FileHandler extends FileHandler {

	private static byte			MASK_MPEG_AUDIO_VERSION_ID	= (byte) 0x18;
	private static byte			MASK_LAYER_DESCRIPTION		= (byte) 0x06;
	private static byte			MASK_PROTECTION_BIT			= (byte) 0x01;

	private static byte			MASK_BITRATE				= (byte) 0xF0;
	private static byte			MASK_SAMPLING_RATE			= (byte) 0x0C;
	private static byte			MASK_PADDING_BIT			= (byte) 0x02;
	private static byte			MASK_PRIVATE_BIT			= (byte) 0x01;

	private static byte			MASK_CHANNEL_MODE			= (byte) 0xC0;
	private static byte			MASK_MODE_EXTENSION			= (byte) 0x30;
	private static byte			MASK_COPYRIGHT				= (byte) 0x08;
	private static byte			MASK_ORIGINAL				= (byte) 0x04;
	private static byte			MASK_EMPHASYS				= (byte) 0x03;

	private static byte			HEADER_START_MASK			= (byte) 0xE0;

	private static int[][]		VL_BITRATE_MAP				= { { 0, 4, 4, 3 }, { 0, 0, 0, 0 }, { 0, 4, 4, 3 }, { 0, 2, 1, 0 } };

	private static int[][]		BITRATE_TABLE				= {
																	{ 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448, 1 },
																	{ 0, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384, 1 },
																	{ 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 1 },
																	{ 0, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256, 1 },
																	{ 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160, 1 } };

	private static int[]		V_SAMPLING_MAP				= { 2, 0, 1, 0 };

	private static int[][]		SAMLPLING_RATE_TABLE		= { { 44100, 48000, 32000, 0 }, { 22050, 24000, 16000, 0 }, { 11025, 12000, 8000, 0 } };

	private static final byte	MASK_ONE_R					= (byte) 0x80;
	private static final byte	MASK_ZERO_R					= (byte) 0x7f;

	private byte[]				mByteData;
	private int					mFirstHeaderPlace;
	private int					mFrameCount;
	private int					mAvailableBits;
	private int					mUnpaddedFrames;

	@Override
	protected void loadFile(File file) {

		try
		{
			mByteData = Files.readAllBytes(file.toPath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// find first frame header index
		mFirstHeaderPlace = findFirstHeader(mByteData);
		int currentHeaderPlace = mFirstHeaderPlace;

		mFrameCount = 0;
		mUnpaddedFrames = 0;

		while (currentHeaderPlace < mByteData.length)
		{
			// calculate frame length
			MP3Header header = readHeader(mByteData, currentHeaderPlace);
			// If the reader returned null, it reached the last frame and the loop will end
			if (header == null)
			{
				break;
			}
			if (header.getPaddingBit() == (byte) 0x00)
			{
				mUnpaddedFrames++;
			}

			// Logger.println("Frame length: " + header.getFrameLength() + ", From: " + currentHeaderPlace + ", padding: " + header.getPaddingBit());

			currentHeaderPlace += header.getFrameLength();

			mFrameCount++;
			mAvailableBits += header.getAvailableBits();
		}

		// Logger.println("Frame count: " + mFrameCount + ", Padding bit frames: " + mUnpaddedFrames);
	}

	@Override
	public int getFileSize() {
		int fileSize = 0;

		fileSize = mAvailableBits - getReservedFileData();

		return fileSize;
	}

	@Override
	public int getTextSize() {
		int textSize = 0;

		textSize = mAvailableBits;

		return textSize;
	}

	@Override
	public String exposeFile() {
		int bitPlace = 0;
		int headerPlace = mFirstHeaderPlace;

		MP3Header header = readHeader(mByteData, headerPlace);

		String fileType = "";
		for (int i = 0; i < (RESERVED_FILE_EXTENSION / 8); i++)
		{
			byte currentByte = 0x00;

			for (int j = 0; j < 8; j++)
			{
				int bit = 0;
				if ((bit = header.getBit(bitPlace)) >= 0)
				{
					bitPlace++;
				}
				else
				{
					headerPlace += header.getFrameLength();
					header = readHeader(mByteData, headerPlace);
					bitPlace = 0;

					bit = header.getBit(bitPlace++);
				}

				currentByte = (byte) (currentByte >> 1);
				currentByte = (byte) ((bit > 0) ? currentByte | MASK_ONE_R : currentByte & MASK_ZERO_R);
			}

			if (currentByte != ' ')
			{
				fileType += (char) currentByte;
			}
		}

		byte[] sizeBytes = new byte[RESERVED_FILE_SIZE / 8];
		for (int i = 0; i < sizeBytes.length; i++)
		{
			byte currentByte = 0x00;

			for (int j = 0; j < 8; j++)
			{
				int bit = 0;
				if ((bit = header.getBit(bitPlace)) >= 0)
				{
					bitPlace++;
				}
				else
				{
					headerPlace += header.getFrameLength();
					header = readHeader(mByteData, headerPlace);
					bitPlace = 0;

					bit = header.getBit(bitPlace++);
				}

				currentByte = (byte) (currentByte >> 1);
				currentByte = (byte) ((bit > 0) ? currentByte | MASK_ONE_R : currentByte & MASK_ZERO_R);
			}

			sizeBytes[i] = currentByte;
		}
		int fileSize = ByteBuffer.wrap(sizeBytes).getInt();

		byte[] fileBytes = new byte[fileSize];
		for (int i = 0; i < fileBytes.length; i++)
		{
			byte currentByte = 0x00;

			for (int j = 0; j < 8; j++)
			{
				int bit = 0;
				if ((bit = header.getBit(bitPlace)) >= 0)
				{
					bitPlace++;
				}
				else
				{
					headerPlace += header.getFrameLength();
					header = readHeader(mByteData, headerPlace);
					bitPlace = 0;

					bit = header.getBit(bitPlace++);
				}

				currentByte = (byte) (currentByte >> 1);
				currentByte = (byte) ((bit > 0) ? currentByte | MASK_ONE_R : currentByte & MASK_ZERO_R);
			}

			fileBytes[i] = currentByte;
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

		int bitPlace = 0;
		int headerPlace = mFirstHeaderPlace;

		MP3Header header = readHeader(mByteData, headerPlace);

		while (header != null)
		{
			byte currentByte = 0x00;

			for (int j = 0; j < 8; j++)
			{
				int bit = 0;
				if ((bit = header.getBit(bitPlace)) >= 0)
				{
					bitPlace++;
				}
				else
				{
					headerPlace += header.getFrameLength();
					header = readHeader(mByteData, headerPlace);
					bitPlace = 0;

					bit = header.getBit(bitPlace++);
				}

				currentByte = (byte) (currentByte >> 1);
				currentByte = (byte) ((bit > 0) ? currentByte | MASK_ONE_R : currentByte & MASK_ZERO_R);
			}

			if (currentByte == '\0')
			{
				break;
			}

			text += (char) currentByte;
			Logger.print((char) currentByte);
		}

		return text;
	}

	@Override
	protected void putBytes(byte[] bytes, File destination) throws IOException {
		int bitPlace = 0;
		int headerPlace = mFirstHeaderPlace;

		MP3Header header = readHeader(mByteData, headerPlace);

		for (int i = 0; i < bytes.length; i++)
		{
			byte currentByte = bytes[i];
			Logger.println("byte * " + (int) (currentByte));

			for (int j = 0; j < 8; j++)
			{
				boolean currentBit = currentByte % 2 != 0;
				if (header.putBit(currentBit, bitPlace))
				{
					bitPlace++;
				}
				else
				{
					saveHeader(header, mByteData, headerPlace);

					headerPlace += header.getFrameLength();
					header = readHeader(mByteData, headerPlace);
					bitPlace = 0;

					header.putBit(currentBit, bitPlace++);
				}

				currentByte = (byte) (currentByte >> 1);
			}

		}

		saveHeader(header, mByteData, headerPlace);

		try
		{
			Files.write(destination.toPath(), mByteData);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private void saveHeader(MP3Header header, byte[] data, int place) {
		byte[] headerBytes = header.getHeaderBytes();

		for (int i = 0; i < headerBytes.length; i++)
		{
			data[place + i] = headerBytes[i];
		}

		if (header.getPaddingBit() == 0x00)
		{
			int paddingPlace = place + header.getFrameLength() - 1;
			data[paddingPlace] = header.getPaddingByte();
		}
	}

	/**
	 * Find the first MP3 Frame header
	 * 
	 * @param h
	 * @return
	 */
	private static int findFirstHeader(byte[] data) {

		int nullCount = 0;
		int startPlace = 0;

		// find zero sequence
		for (int i = 0; i < data.length - 1; i++)
		{

			if (data[i] == (byte) 0x00 && data[i + 1] == (byte) 0x00)
			{
				nullCount++;
			}
			else
			{
				nullCount = 0;
			}
			if (nullCount == 20)
			{
				// System.out.println("found 20 nulls at place " + i);
				startPlace = i;
				break;
			}
		}

		int currentHeader = 0;

		// find first header
		for (int j = startPlace; j < data.length - 1; j++)
		{
			if (data[j] == (byte) 0xFF && (data[j + 1] & HEADER_START_MASK) == HEADER_START_MASK)
			{
				Logger.println("found first header at place: " + j);
				currentHeader = j;
				// System.out.printf("0x%02X", data[i+1]);
				break;
			}
		}

		return currentHeader;
	}

	/**
	 * Read header data
	 * 
	 * @return
	 */
	private static MP3Header readHeader(byte[] header, int indexFrom) {
		MP3Header h = new MP3Header();

		// Check if the frame contains a header
		if (header[indexFrom] == (byte) 0xFF && (header[indexFrom + 1] & HEADER_START_MASK) == HEADER_START_MASK)
		{
			// Get MPEG audio version
			h.setAudioVersion((byte) (((header[indexFrom + 1] & MASK_MPEG_AUDIO_VERSION_ID) >>> 3) & 0x00000003));
			h.setLayerDescription((byte) (((header[indexFrom + 1] & MASK_LAYER_DESCRIPTION) >>> 1) & 0x00000003));
			h.setProtectionBit((byte) ((header[indexFrom + 1] & MASK_PROTECTION_BIT) & 0x00000001));
			h.setBitRate((byte) (((header[indexFrom + 2] & MASK_BITRATE) >>> 4) & 0x0000000F));
			h.setSamplingRate((byte) (((header[indexFrom + 2] & MASK_SAMPLING_RATE) >>> 2) & 0x00000003));
			h.setPaddingBit((byte) (((header[indexFrom + 2] & MASK_PADDING_BIT) >>> 1) & 0x00000001));
			h.setPrivateBit((byte) ((header[indexFrom + 2] & MASK_PRIVATE_BIT) & 0x00000001));
			h.setChannelMode((byte) (((header[indexFrom + 3] & MASK_CHANNEL_MODE) >>> 6) & 0x00000003));
			h.setModeExtension((byte) (((header[indexFrom + 3] & MASK_MODE_EXTENSION) >>> 4) & 0x00000003));
			h.setCopyright((byte) (((header[indexFrom + 3] & MASK_COPYRIGHT) >>> 3) & 0x00000001));
			h.setOriginal((byte) (((header[indexFrom + 3] & MASK_ORIGINAL) >>> 2) & 0x00000001));
			h.setEmphasis((byte) ((header[indexFrom + 3] & MASK_EMPHASYS) & 0x00000003));

			h.setFrameLength(calcFrameLength(h));

			if (h.getPaddingBit() == 0x00)
			{
				h.setPaddingByte(header[indexFrom + h.getFrameLength() - 1]);
			}
		}
		else
		{
			return null;
		}

		return h;
	}

	/**
	 * Calculate MP3 Frame length according to http://mpgedit.org/mpgedit/mpeg_format/mpeghdr.htm
	 * 
	 * @param header
	 * @return frame length
	 */
	private static int calcFrameLength(MP3Header h) {
		int frameLength = 0;

		int index = VL_BITRATE_MAP[h.getAudioVersion()][h.getLayerDescription()];
		int bitRate = BITRATE_TABLE[index][h.getBitRate()] * 1000;

		index = V_SAMPLING_MAP[h.getAudioVersion()];
		int samplingRate = SAMLPLING_RATE_TABLE[index][h.getSamplingRate()];

		int padding = h.getPaddingBit();

		if (h.getLayerDescription() == 3)
		{
			frameLength = (12 * bitRate / samplingRate + padding) * 4;
		}
		else
		{
			frameLength = 144 * bitRate / samplingRate + padding;
		}

		return frameLength;
	}

}
