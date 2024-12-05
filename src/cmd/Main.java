package cmd;
//Budokai Tenkaichi 3 Progress Character Parameter Editor by ViveTheModder
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main 
{
	private static final String[] FILENAMES = {"resident_chara_param.pak","00_progress_chara_param.dat"};
	static final String[] OPTIONS =
	{
		"COM Modifier (Unused)","Character State of Heart","Costume Amount","Destruction Point Amount","Available Slot Amount",
		"Slot Prices","Restricted Character IDs","Camera Values","Read Character Parameters"
	};
	static boolean applyToAll=false, isForWii=false, performFromPak=false;
	public static void main(String[] args) 
	{
		Scanner sc = new Scanner(System.in);
		File src;
		boolean argsInvalid=false, saveFromFile=false;
		int charaID=0, comVal=0, inputArrLen=0, max=0, optionNum;
		byte[] charaIDs = new byte[4];
		float[] cameraVals = new float[3];
		int[] slotPrices = new int[7];
		short[] optionsShort = new short[4];
		
		//validate arguments if the user has provided them (program still works without them)
		if (args.length!=0)
		{
			if (args.length==2)
			{
				for (String arg: args)
				{
					if (arg.equals("-a")) applyToAll=true;
					else if (arg.equals("-w")) isForWii=true;
					else argsInvalid=true;
				}
			}
			else if (args.length==1)
			{
				if (args[0].equals("-h"))
				{
					System.out.println("Edit the contents of 00_progress_chara_param.dat either directly or from the PAK it comes from: resident_chara_param.pak.\n"
					+ "This program can take up to 2 arguments, and their order does not really matter. Here is the list of possible arguments:\n"
					+ "-a -> Apply To All (if not provided, the changes will apply to only one character which the user will later specify.);\n"
					+ "-w -> Enable Wii Mode (meant to be used for files that use the Big Endian byte order than Little Endian, which is what PS2 BT3 files use.);\n"
					+ "-h -> Display Help Message");
					System.exit(0);
				}
				else if (args[0].equals("-a")) applyToAll=true;
				else if (args[0].equals("-w")) isForWii=true;
				else argsInvalid=true;
			}
			else
			{
				System.out.println("Invalid number of arguments ("+args.length+")! The program can only take up to 2 arguments.");
				System.exit(1);
			}
			if (argsInvalid)
			{
				System.out.println("Invalid argument(s)! Next time, enter -h and check the possible list of arguments.");
				System.exit(2);
			}
			else System.out.println("Apply To All: "+applyToAll+"\nWii Mode: "+isForWii+"\n");
		}
		while (true) //validate path
		{
			System.out.println("Enter a valid path pointing to either:\na) "+FILENAMES[0]+"\nb) "+FILENAMES[1]);
			String path = sc.nextLine();
			src = new File(path);
			boolean hasValidName = path.endsWith(FILENAMES[0]) || path.endsWith(FILENAMES[1]);
			if (hasValidName && src.isFile()) 
			{
				if (path.endsWith(FILENAMES[0])) performFromPak=true;
				else performFromPak=false;
				break;
			}
			else System.out.println("Invalid path. Try again!\n");
		}
		while (true) //validate option number
		{
			System.out.println("Enter one of the following options:");
			for (int i=0; i<OPTIONS.length; i++)
			{
				if (i!=OPTIONS.length-1) System.out.println(i+". Change "+OPTIONS[i]);
				else System.out.println(i+". "+OPTIONS[i]);
			}
			String input = sc.nextLine();
			if (input.matches("\\d+") && input.length()==1)
			{
				optionNum = Integer.parseInt(input);
				if (optionNum<OPTIONS.length) break;
				else System.out.println("Option number does not correspond to any option. Try again!\n");
			}
			else System.out.println("Invalid format for option (not a number). Try again!\n");
		}
		ProgressCharaParam pcp = new ProgressCharaParam(src);
		switch (optionNum) //get additional inputs depending on selected option
		{
			case 0:
			while (true)
			{
				System.out.println("Enter a COM Value between 0 and 2 (other values are discouraged):");
				String input = sc.nextLine();
				if (input.matches("[0-2]+") && input.length()==1) 
				{
					comVal=Integer.parseInt(input);
					break;
				}
				else System.out.println("Invalid format or discouraged value. Try again!\n");
			}
			break;
			case 1: 
			while (true)
			{
				System.out.println("Enter either 1 (pure good) or 2 (pure evil):");
				String input = sc.nextLine();
				if (input.matches("[1-2]+") && input.length()==1) 
				{
					optionsShort[0] = (short) Integer.parseInt(input);
					break;
				}
				else System.out.println("Invalid state of heart. Try again!\n");
			}
			break;
			case 2:
			while (true)
			{
				System.out.println("Enter a costume amount from 2 to 4 (1 or greater than 4 is discouraged):");
				String input = sc.nextLine();
				if (input.matches("[2-4]+") && input.length()==1)
				{
					optionsShort[1] = (short) Integer.parseInt(input);
					break;
				}
				else System.out.println("Invalid or discouraged costume amount. Try again!\n");
			}
			break;
			case 3:
			while (true)
			{
				System.out.println("Enter a Destruction Point amount from 1 to 15 (0 or greater than 15 is discouraged):");
				String input = sc.nextLine();
				if (input.matches("\\d+"))
				{
					int temp = Integer.parseInt(input);
					if (temp>0 && temp<16)
					{
						optionsShort[2] = (short)temp;
						break;
					}
					else System.out.println("Discouraged amount of Destruction Points. Try again!\n");
				}
				else System.out.println("Invalid amount of Destruction Points. Try again!\n");
			}
			break;
			case 4:
			while (true)
			{
				System.out.println("Enter an available slot amount from 1 to 8:");
				String input = sc.nextLine();
				if (input.matches("[1-8]+") && input.length()==1)
				{
					optionsShort[3] = (short) Integer.parseInt(input);
					break;
				}
				else System.out.println("Invalid available slot amount. Try again!\n");
			}
			break;
			case 5:
			while (true)
			{
				System.out.println("Enter up to 7 slot prices, separated by spaces:");
				String input = sc.nextLine();
				if (input.matches("^\\d+(\\s+\\d+)*$"))
				{
					String[] inputArr = input.split(" ");
					inputArrLen = inputArr.length;
					if (inputArrLen>slotPrices.length) inputArrLen=slotPrices.length;
					for (int i=0; i<inputArrLen; i++)
						slotPrices[i]=Integer.parseInt(inputArr[i]);
					break;
				}
				else System.out.println("Invalid delimiter (not a space) or invalid slot price format (not a number). Try again!\n");
			}
			break;
			case 6:
			while (true)
			{
				try 
				{
					max = ProgressCharaParam.getCharaCnt()-1;
				} 
				catch (IOException e) 
				{
					ProgressCharaParam.setErrorLog(e);
				}
				System.out.println("Enter up to 4 Restricted Character IDs, separated by spaces, where each ID ranges from 0 to "+max+":");
				String input = sc.nextLine();
				if (input.matches("^\\d+(\\s+\\d+)*$"))
				{
					String[] inputArr = input.split(" ");
					inputArrLen = inputArr.length;
					if (inputArrLen>charaIDs.length) inputArrLen=charaIDs.length;
					for (int i=0; i<inputArrLen; i++)
					{
						charaIDs[i]=(byte) Integer.parseInt(inputArr[i]);
						if (charaIDs[i]>max) charaIDs[i]=(byte)max;
					}
					break;
				}
				else System.out.println("Invalid delimiter (not a space) or invalid character ID format (not a number). Try again!\n");
			}
			break;
			case 7:
			while (true)
			{
				System.out.println("Enter up to 3 decimal numbers, one for each axis (XYZ, in this order), separated by spaces:");
				String input = sc.nextLine();
				if (input.matches("^[+-]?\\d*\\.\\d+(\\s+[+-]?\\d*\\.\\d+)*$"))
				{
					String[] inputArr = input.split(" ");
					inputArrLen = inputArr.length;
					if (inputArrLen>cameraVals.length) inputArrLen=cameraVals.length;
					for (int i=0; i<inputArrLen; i++)
						cameraVals[i] = Float.parseFloat(inputArr[i]);
					break;
				}
				else System.out.println("Invalid decimal number amount and/or invalid format (must contain only numbers). Try again!\n");
			}
			case 8:
			while (true)
			{
				System.out.println("Read character parameters right away (N) or also save them in a file (Y)?");
				String input = sc.nextLine();
				if (input.equals("N"))
				{
					saveFromFile=false;
					break;
				}
				else if (input.equals("Y")) 
				{
					saveFromFile=true;
					break;
				}
				else System.out.println("Invalid input. Try again!\n");
			}
			break;
		}
		if (optionNum<8 && !applyToAll) //get character ID if "-a" argument has not been entered
		{
			while (true)
			{
				if (max==0) //get character count only if it has not been assigned already 
				{
					try 
					{
						max = ProgressCharaParam.getCharaCnt()-1;
					} 
					catch (IOException e) 
					{
						ProgressCharaParam.setErrorLog(e);
					}
				}
				System.out.println("Enter an ID for the character whose parameters you want to overwrite, where the ID ranges from 0 to "+max+":");
				String input = sc.nextLine();
				if (input.matches("\\d+"))
				{
					charaID = Integer.parseInt(input);
					if (charaID>160) charaID=160; //handle overflow
					break;
				}
				else System.out.println("Invalid character ID format (not a number). Try again!\n");
			}
		}
		sc.close();
		long start = System.currentTimeMillis();
		switch (optionNum) //perform read/write action depending on selected option
		{
			case 0:
			pcp.write(LittleEndian.getByteArrayFromInt(comVal), 0, 0, charaID);
			break;
			case 1:
			pcp.write(LittleEndian.getByteArrayFromShort(optionsShort[0]), 8, 0, charaID);
			break;
			case 2:
			pcp.write(LittleEndian.getByteArrayFromShort(optionsShort[1]), 10, 0, charaID);
			break;
			case 3:
			pcp.write(LittleEndian.getByteArrayFromShort(optionsShort[2]), 12, 0, charaID);	
			break;
			case 4:
			pcp.write(LittleEndian.getByteArrayFromShort(optionsShort[3]), 14, 0, charaID);
			break;
			case 5:
			pcp.write(LittleEndian.getByteArrayFromIntArray(slotPrices), 16, inputArrLen, charaID);
			break;
			case 6:
			pcp.write(charaIDs, 44, inputArrLen, charaID);
			break;
			case 7:
			pcp.write(LittleEndian.getByteArrayFromFloatArray(cameraVals), 48, inputArrLen, charaID);
			break;
			case 8:
			pcp.read(saveFromFile);
			break;
		}
		long finish = System.currentTimeMillis();
		System.out.println("\nTime: "+(finish-start)/(double)1000+" s");
	}
}