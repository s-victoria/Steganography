package svictoria.steganography;

import java.io.File;
import java.io.IOException;

public class InputStruct {

	CommandType	command;
	File		sourceFile;
	File		destinationFile;
	String		sourceText;

	public InputStruct() {
		super();
		this.command = null;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(String sourceFileName) {
		this.sourceFile = new File(sourceFileName);
	}

	public File getDestinationFile() {
		return destinationFile;
	}

	public void setDestinationFile(String destinationFileName) {
		this.destinationFile = new File(destinationFileName);
	}

	public CommandType getCommand() {
		return command;
	}

	public void setCommand(CommandType command) {
		this.command = command;
	}

	public String getSourceText() {
		return sourceText;
	}

	public void setSourceText(String sourceText) {
		this.sourceText = sourceText;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public void setDestinationFile(File destinationFile) {
		this.destinationFile = destinationFile;
	}

	public FileType getSourceFileType() throws IOException {
		String extension = sourceFile.getName().split("[.]")[1];
		return FileType.fromString(extension);
	}

	public FileType getDestinationFileType() throws IOException {
		String extension = destinationFile.getName().split("[.]")[1];
		return FileType.fromString(extension);
	}

	public FileType getWorkingFileType() throws IOException {
		if (command.equals(CommandType.SIZE_FILE) || command.equals(CommandType.SIZE_TEXT) || command.equals(CommandType.EXPOSE_FILE) || command
				.equals(CommandType.EXPOSE_TEXT))
		{
			return getSourceFileType();
		}
		else
		{
			return getDestinationFileType();
		}
	}

}
