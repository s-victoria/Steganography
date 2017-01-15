package svictoria.steganography;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InputProcessor {

	public InputStruct processInput(String[] args) throws Exception {

		InputStruct processedInput = new InputStruct();

		// Check if there is a legal amount of arguments
		if (args.length <= 3)
		{
			// Check if the first argument is a legal command
			if (isLegalCommand(args[0]))
			{
				// Check if the source file exists
				if (isLegalFile(args[1]))
				{
					String commandStr = args[0];
					CommandType command = CommandType.fromString(commandStr);
					processedInput.setCommand(command);
					processedInput.setSourceFile(args[1]);

					if (command.equals(CommandType.SIZE_FILE) || command.equals(CommandType.SIZE_TEXT) || command
							.equals(CommandType.EXPOSE_FILE) || command.equals(CommandType.EXPOSE_TEXT))
					{
						// Check if there is a right amount of arguments
						if (args.length != 2)
						{
							throw new InputException(InputError.ARGUMENTS_OVERLOAD.toString());
						}
					}
					else if (command.equals(CommandType.HIDE_FILE) || command.equals(CommandType.HIDE_TEXT))
					{
						// Check if there is a right amount of arguments
						if (args.length == 3)
						{
							if (isLegalFile(args[2]))
							{
								processedInput.setDestinationFile(args[2]);
								if (command.equals(CommandType.HIDE_TEXT))
								{
									String sourceText = getTextFromFile(processedInput.getSourceFile());
									processedInput.setSourceText(sourceText);
								}
							}
							else
							{
								throw new InputException(InputError.ILLEGAL_DESTINATION.toString());
							}
						}
						else
						{
							throw new InputException(InputError.ARGUMENTS_OVERLOAD.toString());
						}
					}
				}
				else
				{
					throw new InputException(InputError.ILLEGAL_SOURCE.toString());
				}
			}
			else
			{
				throw new InputException(InputError.ILLEGAL_COMMAND.toString());
			}
		}
		else
		{
			throw new InputException(InputError.ARGUMENTS_OVERLOAD.toString());
		}

		return processedInput;

	}

	private boolean isLegalCommand(String command) {
		for (CommandType c : CommandType.values())
		{
			if (c.toString().equals(command))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isLegalFile(String fileName) {
		File f = new File(fileName);
		if (f.exists())
		{
			return true;
		}
		return false;
	}

	private String getTextFromFile(File sourceFile) throws Exception {
		if ("txt".equals(sourceFile.getName().split("[.]")[1]))
		{
			String content = new String(Files.readAllBytes(Paths.get(sourceFile.getPath())));
			return content;
		}
		else
		{
			throw new Exception(InputError.SOURCE_NOT_TEXT.toString());
		}
		
	}

}
