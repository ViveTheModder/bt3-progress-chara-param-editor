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
	private RandomAccessFile pcp;
	private String[] charaNames;
	public ProgressCharaParam(File src) 
	{
		try 
		{
			this.pcp = new RandomAccessFile(src,"rw");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
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
	private void setErrorLog(Exception e)
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
	public void read()
	{
		try
		{
			int pos=0;
			setCharaNames();
			if (Main.performFromPak)
			{
				pcp.seek(4);
				pos = LittleEndian.getInt(pcp.readInt()); //get address from PAK file index
			}
			pcp.seek(pos);
			for (int i=0; i<161; i++)
			{
				System.out.println("["+charaNames[i]+"]");
				System.out.println("* "+Main.OPTIONS[0]+": "+LittleEndian.getInt(pcp.readInt()));
				pcp.readInt(); //skip 40000, an unused value that every character has
				int stateOfHeartVal = LittleEndian.getShort(pcp.readShort());
				String stateOfHeart = "Evil";
				if (stateOfHeartVal==1) stateOfHeart = "Pure";
				System.out.println("* "+Main.OPTIONS[1]+": "+stateOfHeart);
				for (int j=2; j<5; j++)
					System.out.println("* "+Main.OPTIONS[j]+": "+LittleEndian.getShort(pcp.readShort()));
				System.out.println("* "+Main.OPTIONS[5]);
				for (int j=0; j<7; j++)
					System.out.println("-> Slot #"+(j+1)+": "+LittleEndian.getInt(pcp.readInt()));
				System.out.println("* "+Main.OPTIONS[6].replace(" ID", ""));
				for (int j=0; j<4; j++)
				{
					int index = pcp.readUnsignedByte();
					String name=null;
					if (index!=255) name=charaNames[index];
					System.out.println("-> Character #"+(j+1)+": "+name);
				}
				System.out.println("* "+Main.OPTIONS[7]);
				for (int j=0; j<3; j++)
					System.out.println("-> Camera Value #"+(j+1)+": "+LittleEndian.getFloat(pcp.readFloat()));
			}
		}
		catch (IOException e)
		{
			setErrorLog(e);
		}
	}
	public void write(byte[] arr, int offset, int charaID)
	{
		try
		{
			int fileSize, pos=0, nextPos=0;
			if (Main.performFromPak)
			{
				pcp.seek(4);
				pos = LittleEndian.getInt(pcp.readInt());
				nextPos = LittleEndian.getInt(pcp.readInt());
				fileSize = nextPos;
			}
			else fileSize = (int) pcp.length();
			pcp.seek(pos);
			for (int i=0; i<161; i++)
			{
				if (Main.applyToAll) charaID=i;
				if (i==charaID)
				{
					pcp.seek(pos+(i*60)+offset);
					if (pcp.getFilePointer()>=fileSize) return; //more or less an EOFException but not really
					pcp.write(arr);
				}
			}
		}
		catch (IOException e)
		{
			setErrorLog(e);
		}
	}
}