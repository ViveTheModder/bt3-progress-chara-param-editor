package cmd;
//Little Endian class by ViveTheModder
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LittleEndian 
{
	public static byte[] getByteArrayFromFloatArray(float[] data)
	{
		ByteBuffer bb = ByteBuffer.allocate(data.length*4);
		if (!Main.isForWii) bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asFloatBuffer().put(data);
		return bb.array();
	}
	public static byte[] getByteArrayFromInt(int data)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		if (!Main.isForWii) bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asIntBuffer().put(data);
		return bb.array();
	}
	public static byte[] getByteArrayFromIntArray(int[] data)
	{
		ByteBuffer bb = ByteBuffer.allocate(data.length*4);
		if (!Main.isForWii) bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asIntBuffer().put(data);
		return bb.array();
	}
	public static byte[] getByteArrayFromShort(short data)
	{
		ByteBuffer bb = ByteBuffer.allocate(2);
		if (!Main.isForWii) bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.asShortBuffer().put(data);
		return bb.array();
	}
	public static float getFloat(float data)
	{
		if (Main.isForWii) return data;
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.asFloatBuffer().put(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}
	public static int getInt(int data)
	{
		if (Main.isForWii) return data;
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.asIntBuffer().put(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}
	public static short getShort(short data)
	{
		if (Main.isForWii) return data;
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.asShortBuffer().put(data);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort();
	}
}