package bizbrolly.svarochiapp.ibahn_logic;

/**
 * Created by bizbrolly on 9/21/16.
 */
public enum Data {
    POWER_ON("IBP1"),
    POWER_OFF("IBP0"),
    TUNING("IBW"),
    INTENSITY("IBI"),
    COLOR("IBR");
    private final String dataValue;

    private Data(String value){
        dataValue = value;
    }

    public String getDataValue(){
//        if(dataValue.equals("VRGB"))
//            throw new RuntimeException("Please insert rgb values in parameters for sending RGB values!");
        return dataValue;
    }

    public byte[] getDataValue(int i, int r, int g, int b){
        if(!dataValue.equals("IBR"))
            throw new RuntimeException("This method should only be used for sending RGB values!");
        byte[] vrByteArray = "IBR".getBytes();
        byte[] dataToSend = new byte[vrByteArray.length + 5];
//        Log.e("VGuardData","i ; "+i+"r ; "+r+"g ; "+g+"b ; "+b);
        System.arraycopy(vrByteArray, 0, dataToSend, 0, vrByteArray.length);
        dataToSend[vrByteArray.length] = (byte) ((r*255f)/(r+g+b));
        dataToSend[vrByteArray.length+1] = (byte) ((g*255f)/(r+g+b));
        dataToSend[vrByteArray.length+2] = (byte) ((b*255f)/(r+g+b));
        dataToSend[vrByteArray.length+3] = (byte) (0);
        dataToSend[vrByteArray.length+4] = (byte) (i);
        return dataToSend;
    }

}
