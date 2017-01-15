package svictoria.steganography;

public class MP3Header {

	// Expandable bits: private, copyright, original, emphasis

	private static final int	MAX_OVERRIDE_BITS	= 3;

	private static final int	PRIVATE_BIT			= 0;
	private static final int	COPYRIGHT_BIT		= 1;
	private static final int	ORIGINAL_BIT		= 2;
													// private static final int EMPHASIS_1_BIT = 3;
													// private static final int EMPHASIS_2_BIT = 4;

	// private static final byte MASK_EMPHASIS_1_ONE = (byte) 0x02;
	// private static final byte MASK_EMPHASIS_1_ZERO = (byte) 0xFD;
	// private static final byte MASK_EMPHASIS_2_ONE = (byte) 0x01;
	// private static final byte MASK_EMPHASIS_2_ZERO = (byte) 0xFE;

	private static final byte	MASK_ONE_R			= (byte) 0x80;
	private static final byte	MASK_ZERO_R			= (byte) 0x7f;

	private int					frameLength			= -1;

	private byte				paddingByte			= (byte) 0x00;	// Empty padding byte

	private byte				audioVersion		= (byte) 0x00;
	private byte				layerDescription	= (byte) 0x00;
	private byte				protectionBit		= (byte) 0x00;
	private byte				bitRate				= (byte) 0x00;
	private byte				samplingRate		= (byte) 0x00;
	private byte				paddingBit			= (byte) 0x00;
	private byte				privateBit			= (byte) 0x00;	// First overridden bit
	private byte				channelMode			= (byte) 0x00;
	private byte				modeExtension		= (byte) 0x00;
	private byte				copyright			= (byte) 0x00;	// Second overridden bit
	private byte				original			= (byte) 0x00;	// Third overridden bit
	private byte				emphasis			= (byte) 0x00;	// Fourth - Fifth overridden bits

	public boolean putBit(boolean bit, int place) {
		if (place >= getAvailableBits())
		{
			return false;
		}

		switch (place)
		{
			case PRIVATE_BIT:
				setPrivateBit((bit) ? (byte) 0x01 : 0x00);
				break;
			case COPYRIGHT_BIT:
				setCopyright((bit) ? (byte) 0x01 : 0x00);
				break;
			case ORIGINAL_BIT:
				setOriginal((bit) ? (byte) 0x01 : 0x00);
				break;
			default:
				putInPaddingByte(bit, place - MAX_OVERRIDE_BITS);
				// case EMPHASIS_1_BIT:
				// setEmphasis((byte) ((bit) ? getEmphasis() | MASK_EMPHASIS_1_ONE : getEmphasis() & MASK_EMPHASIS_1_ZERO));
				// break;
				// case EMPHASIS_2_BIT:
				// setEmphasis((byte) ((bit) ? getEmphasis() | MASK_EMPHASIS_2_ONE : getEmphasis() & MASK_EMPHASIS_2_ZERO));
				// break;
		}

		return true;
	}

	private void putInPaddingByte(boolean bit, int place) {
		if (bit)
		{
			paddingByte = (byte) (paddingByte | (1 << place));
		}
		else
		{
			paddingByte = (byte) (paddingByte & ~(1 << place));
		}
	}

	public int getBit(int place) {
		if (place >= getAvailableBits())
		{
			return -1;
		}

		switch (place)
		{
			case PRIVATE_BIT:
				return (getPrivateBit() == 0x00) ? 0 : 1;
			case COPYRIGHT_BIT:
				return (getCopyright() == 0x00) ? 0 : 1;
			case ORIGINAL_BIT:
				return (getOriginal() == 0x00) ? 0 : 1;
			default:
				return getInPaddingByte(place - MAX_OVERRIDE_BITS);
			// case EMPHASIS_1_BIT:
			// return ((byte) (getEmphasis() & MASK_EMPHASIS_1_ONE) == 0x00) ? 0 : 1;
			// case EMPHASIS_2_BIT:
			// return ((byte) (getEmphasis() & MASK_EMPHASIS_2_ONE) == 0x00) ? 0 : 1;
		}
	}

	private int getInPaddingByte(int place) {
		return ((paddingByte & (1 << place)) > 0x00) ? 1 : 0;
	}

	public byte[] getHeaderBytes() {
		byte[] bytes = new byte[4];

		bytes[0] = (byte) 0xFF;

		bytes[1] = (byte) 0xE0;
		bytes[1] |= (byte) (audioVersion << 3);

		bytes[1] |= (byte) (layerDescription << 1);
		bytes[1] |= (byte) (protectionBit);

		bytes[2] = 0x00;
		bytes[2] |= (byte) (bitRate << 4);
		bytes[2] |= (byte) (samplingRate << 2);
		bytes[2] |= (byte) (paddingBit << 1);
		bytes[2] |= (byte) (privateBit);

		bytes[3] = 0x00;
		bytes[3] |= (byte) (channelMode << 6);
		bytes[3] |= (byte) (modeExtension << 4);
		bytes[3] |= (byte) (copyright << 3);
		bytes[3] |= (byte) (original << 2);
		bytes[3] |= (byte) (emphasis);

		return bytes;
	}

	public int getAvailableBits() {
		int paddingBits = (paddingBit == 0x00) ? 8 : 0;
		return MAX_OVERRIDE_BITS + paddingBits;
	}

	public int getFrameLength() {
		return frameLength;
	}

	public void setFrameLength(int frameLength) {
		this.frameLength = frameLength;
	}

	public byte getAudioVersion() {
		return audioVersion;
	}

	public void setAudioVersion(byte audioVersion) {
		this.audioVersion = audioVersion;
	}

	public byte getLayerDescription() {
		return layerDescription;
	}

	public void setLayerDescription(byte layerDescription) {
		this.layerDescription = layerDescription;
	}

	public byte getProtectionBit() {
		return protectionBit;
	}

	public void setProtectionBit(byte protectionBit) {
		this.protectionBit = protectionBit;
	}

	public byte getBitRate() {
		return bitRate;
	}

	public void setBitRate(byte bitRate) {
		this.bitRate = bitRate;
	}

	public byte getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(byte samplingRate) {
		this.samplingRate = samplingRate;
	}

	public byte getPaddingBit() {
		return paddingBit;
	}

	public void setPaddingBit(byte paddingBit) {
		this.paddingBit = paddingBit;
	}

	public byte getPrivateBit() {
		return privateBit;
	}

	public void setPrivateBit(byte privateBit) {
		this.privateBit = privateBit;
	}

	public byte getChannelMode() {
		return channelMode;
	}

	public void setChannelMode(byte channelMode) {
		this.channelMode = channelMode;
	}

	public byte getModeExtension() {
		return modeExtension;
	}

	public void setModeExtension(byte modeExtension) {
		this.modeExtension = modeExtension;
	}

	public byte getCopyright() {
		return copyright;
	}

	public void setCopyright(byte copyright) {
		this.copyright = copyright;
	}

	public byte getOriginal() {
		return original;
	}

	public void setOriginal(byte original) {
		this.original = original;
	}

	public byte getEmphasis() {
		return emphasis;
	}

	public void setEmphasis(byte emphasis) {
		this.emphasis = emphasis;
	}
	
	public byte getPaddingByte() {
		return paddingByte;
	}
	
	public void setPaddingByte(byte paddingByte) {
		this.paddingByte = paddingByte;
	}

}