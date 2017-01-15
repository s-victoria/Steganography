package svictoria.steganography;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		Logger.DEBUG = false;

		try
		{
			InputProcessor proc = new InputProcessor();
			InputStruct input = proc.processInput(args);
			CommandType command = input.getCommand();
			FileHandler fileHandler = getHandlerByFileType(input.getWorkingFileType());

			switch (command)
			{
				case SIZE_FILE:
					fileHandler.setFile(input.sourceFile);
					int fileSize = fileHandler.getFileSize() / 8;
					Logger.output(fileSize + " bytes available for hiding in " + input.sourceFile.getName());
					break;
				case SIZE_TEXT:
					fileHandler.setFile(input.sourceFile);
					int txtSize = fileHandler.getTextSize() / 8;
					Logger.output(txtSize + " characters available for hiding in " + input.sourceFile.getName());
					break;
				case HIDE_FILE:
					String hiddenFilePath = hideFile(input.sourceFile, input.destinationFile, fileHandler);
					Logger.output("File " + input.sourceFile.getName() + " hidden in " + hiddenFilePath);
					break;
				case HIDE_TEXT:
					String hiddenTextPath = hideText(input.getSourceText(), input.getDestinationFile(), fileHandler);
					Logger.output("Text from " + input.sourceFile.getName() + " hidden in " + hiddenTextPath);
					break;
				case EXPOSE_FILE:
					String exposed = exposeFile(input.sourceFile, fileHandler);
					Logger.output("File " + input.sourceFile.getName() + " exposed to " + exposed);
					break;
				case EXPOSE_TEXT:
					String exposedText = exposeText(input.sourceFile, fileHandler);
					Logger.output(exposedText);
					break;

				default:
					break;
			}
		}
		catch (Exception e)
		{
			Logger.output(e.getMessage());
		}
	}

	protected static FileHandler getHandlerByFileType(FileType fileType) {
		switch (fileType)
		{
			case BMP:
				return new BmpFileHandler();
			case PNG:
				return new PngFileHandler();
			case MP3:
				return new MP3FileHandler();
			default:
				return null;
		}
	}

	protected static String hideFile(File data, File file, FileHandler fileHandler) throws Exception {
		Logger.println("file bytes: " + file.length());

		fileHandler.setFile(file);

		int size = fileHandler.getFileSize();
		Logger.println("file: " + size);

		return fileHandler.hideFile(data);
	}

	protected static String hideText(String text, File file, FileHandler fileHandler) throws Exception {
		Logger.println("file bytes: " + file.length());

		fileHandler.setFile(file);

		int size = fileHandler.getFileSize();
		Logger.println("file: " + size);

		return fileHandler.hideText(text);
	}

	protected static String exposeText(File file, FileHandler fileHandler) {
		fileHandler.setFile(file);

		return fileHandler.exposeText();
	}

	protected static String exposeFile(File file, FileHandler fileHandler) {
		fileHandler.setFile(file);

		return fileHandler.exposeFile();
	}

	protected static boolean filesEquals(File f1, File f2) {
		try
		{
			return Arrays.equals(Files.readAllBytes(f1.toPath()), Files.readAllBytes(f2.toPath()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}
}
