package cmd;
//Progress Character Parameter Object by ViveTheModder
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ProgressCharaParam 
{
	private int charaCnt;
	private static RandomAccessFile pcp;
	private String[] charaNames;
	public ProgressCharaParam(File src) 
	{
		try 
		{
			pcp = new RandomAccessFile(src,"rw");
		} 
		catch (IOException e) 
		{
			setErrorLog(e);
		}
	}
	public static int getCharaCnt() throws IOException
	{
		int cnt=0, fileSize, pos=0, nextPos=0;
		if (Main.performFromPak)
		{
			pcp.seek(4);
			pos = LittleEndian.getInt(pcp.readInt()); //get address from PAK file index
			nextPos = LittleEndian.getInt(pcp.readInt());
			fileSize = nextPos;
		}
		else fileSize = (int) pcp.length();
		
		pcp.seek(pos);
		while (pcp.getFilePointer() != fileSize)
		{
			if (LittleEndian.getInt(pcp.readInt())==40000) cnt++;
		}
		return cnt;
	}
	private void setCharaCnt() throws IOException
	{
		this.charaCnt = getCharaCnt();
	}
	private void setCharaNames() throws IOException
	{
		File charaCsv = new File("./csv/characters.csv").getCanonicalFile();
		Scanner sc = new Scanner(charaCsv);
		charaNames = new String[161];
		while (sc.hasNextLine())
		{
			String input = sc.nextLine();
			String[] inputArr = input.split(",");
			int index = Integer.parseInt(inputArr[0]);
			if (index!=255) charaNames[index] = inputArr[1];
		}
		sc.close();
	}
	public static void setErrorLog(Exception e)
	{
		File errorLog = new File("errors.log");
		try {
			FileWriter logWriter = new FileWriter(errorLog,true);
			logWriter.append(new SimpleDateFormat("dd-MM-yy-hh-mm-ss").format(new Date())+":\n"+e.getMessage()+"\n");
			logWriter.close();
			Desktop.getDesktop().open(errorLog);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(1);
	}
	public void read(boolean saveFromFile)
	{
		try
		{
			int pos=0;
			String text, fullText="";
			setCharaCnt();
			setCharaNames();
			if (Main.performFromPak)
			{
				pcp.seek(4);
				pos = LittleEndian.getInt(pcp.readInt()); //get address from PAK file index
			}
			pcp.seek(pos);
			for (int i=0; i<charaCnt; i++)
			{
				if (i<charaNames.length) text="["+charaNames[i]+"]\n";
				else text="[Unknown Character (ID: "+i+")]\n";
				text+="* "+Main.OPTIONS[0]+": "+LittleEndian.getInt(pcp.readInt())+"\n";
				pcp.readInt(); //skip 40000, an unused value that every character has
				int stateOfHeartVal = LittleEndian.getShort(pcp.readShort());
				String stateOfHeart = "Evil";
				if (stateOfHeartVal==1) stateOfHeart = "Pure";
				text+="* "+Main.OPTIONS[1]+": "+stateOfHeart+"\n";
				for (int j=2; j<5; j++)
					text+="* "+Main.OPTIONS[j]+": "+LittleEndian.getShort(pcp.readShort())+"\n";
				text+="* "+Main.OPTIONS[5]+"\n";
				for (int j=0; j<7; j++)
					text+="-> Slot #"+(j+1)+": "+LittleEndian.getInt(pcp.readInt())+"\n";
				text+="* "+Main.OPTIONS[6].replace(" ID", "")+"\n";
				for (int j=0; j<4; j++)
				{
					int index = pcp.readUnsignedByte();
					String name=null;
					if (index!=255) name=charaNames[index];
					text+="-> Character #"+(j+1)+": "+name+"\n";
				}
				text+="* "+Main.OPTIONS[7]+"\n";
				for (int j=0; j<3; j++)
					text+="-> Camera Value #"+(j+1)+": "+LittleEndian.getFloat(pcp.readFloat())+"\n";		
				fullText+=text;
				if (!saveFromFile) System.out.print(text);
			}
			if (saveFromFile)
			{
				File outputTxt = new File("output.txt");
				FileWriter fw = new FileWriter(outputTxt);
				fw.write(fullText);
				fw.close();
				Desktop.getDesktop().open(outputTxt);
			}
		}
		catch (IOException e)
		{
			setErrorLog(e);
		}
	}
	public void write(byte[] arr, int offset, int length, int charaID)
	{
		try
		{
			int fileSize, pos=0, nextPos=0;
			setCharaCnt();
			if (Main.performFromPak)
			{
				pcp.seek(4);
				pos = LittleEndian.getInt(pcp.readInt());
				nextPos = LittleEndian.getInt(pcp.readInt());
				fileSize = nextPos;
			}
			else fileSize = (int) pcp.length();
			pcp.seek(pos);
			for (int i=0; i<charaCnt; i++)
			{
				if (Main.applyToAll) charaID=i;
				if (i==charaID)
				{
					pcp.seek(pos+(i*60)+offset);
					if (pcp.getFilePointer()>=fileSize) return; //more or less an EOFException but not really
					if (length>0 && length<arr.length) //prevent overwriting remaining values from the array with 0
					{
						byte[] temp = new byte[length];
						System.arraycopy(arr, 0, temp, 0, length);
						pcp.write(temp);
					}
					else pcp.write(arr);
				}
			}
		}
		catch (IOException e)
		{
			setErrorLog(e);
		}
	}
}