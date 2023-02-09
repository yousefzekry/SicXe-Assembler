package Sic;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Exception;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Scanner;

public class SICAssembler {

    static String[][] passed1CodeArray = new String [21][4];
    static String[][] passed2CodeArray = new String [21][5];
    static String[][] SymbolTable = new String[21][2];

    //op, format, opcode in hex
    //format 1 = 8bits
    //format2 = 8bits of opcode + r1 4bits+ r2 4bits
    //format3 = 8bits of opcode + nixbpe rep in 6 bits + displacement represented in 12 bits
    //format4 = 8bits of opcode + nixbpe rep in 6 bits + address represented in 20 bits
    // Name, Format Number, OpCode in Hexa, OpCode in binary
    static String[][] opCodeArray = {{"ADD","3","18"},
        {"ADDF","3","58"},{"ADDR","2","90"},{"AND","3","40"},{"CLEAR","2","4"},
        {"COMP","3","28"},{"COMPF","3","88"},{"COMPR","2","A0"},{"DIV","3","24"},
        {"DIVF","3","64"},{"DIVR","2","9C"},{"FIX","1","C4"},{"FLOAT","1","C0"},
        {"HIO","1","F4"},{"J","3","3C"},{"JEQ","3","30"},{"JGT","3","34"},
        {"JLT","3","38"},{"JSUB","3","48"},{"LDA","3","00"},
        {"LDB","3","68"},{"LDCH","3","50"},{"LDF","3","70"},{"LDL","3","08"},
        {"LDS","3","6C"},{"LDT","3","74"},{"LDX","3","04"},
        {"LPS","3","D0"},{"MUL","3","20"},{"MULF","3","60"},{"MULR","2","98"},
	{"NORM","1","C8"},{"OR","3","44"},{"RD","3","D8"},{"RMO","2","AC"},{"RSUB","3","4C"},{"SHIFTL","2","A4"},
	{"SHIFTR","2","A8"},{"SIO","1","F0"},{"SSK","3","EC"},{"STA","3","0C"},{"STB","3","78"},
	{"STCH","3","54"},{"STF","3","80"},{"STI","3","D4"},{"STL","3","14"},{"STS","3","7C"},
	{"STSW","3","E8"},{"STT","3","84"},{"SUB","3","1C"},{"SUBF","3","5C"},
	{"SUBR","2","94"},{"SVC","2","B0"},{"TD","3","E0"},{"TIO","1","F8"},{"TIX","3","2C"},
	{"TIXR","2","B8"},{"WD","3","DC"}};

static String[][] RegisterTable = {{"A","3"},{"X","1"},{"L","2"},{"PC","8"},{"SW","9"},{"B","3"},{"S","4"},{"T","5"},{"F","6"}};

    //codeArray[Symbol, instruction, reference]
    static String[][] codeArray = new String[21][3];

    static String programName = "";
    static int Start,End,Length1,Length2,End_Address;
    static int symbolTableIndexReserved = 0;
    static int programCounter = 0;
    static String indexedLabel="";
    static String[][] useProc = new String [5][2];
    //use procedure counter
    static int useProcCount = 0;
    //format 1 array
    static String[] format1 = new String [8];
    //format 2 array
    static String[] format2 = new String [16];
    //format 3 array
    static String[] format3 = new String [24];
    //format 4 array
    static String[] format4 = new String [32];
    //main method

    public static void main(String[] args) throws FileNotFoundException {

        String lineRead = "";
        int codeArrayindex1 = 0;
        try (Scanner inFile = new Scanner(new File("test.txt"))) {
            while(inFile.hasNextLine())
            {
                lineRead = inFile.nextLine();
                codeArray[codeArrayindex1] = lineRead.split("\\s+");
                //System.out.print(Arrays.toString(codeArray[codeArrayindex1]));
                codeArrayindex1++;
            }
        }

        pass1();
        printPass1Table();
        pass2();
        printPass2Table();
        printHTE();

    }


    public static void pass1()
    {
        int programStart = 0;
        int TempValueCounter = 0;
        String summedCounter = "";
        String inOperand = "";
        for(int i = 0;i < codeArray.length;i++)
        {

			//
            if(i == 0 && !codeArray[0][0].startsWith("."))
            {
                programCounter = (int) Long.parseLong(codeArray[0][2],16);
                Start = programStart = (int) Long.parseLong(codeArray[0][2],16);
                //location counter
                passed1CodeArray[i][0] = null;

                //symbol
                passed1CodeArray[i][1] = codeArray[0][0];
                //instruction
                passed1CodeArray[i][2] = codeArray[0][1];
                //refrence

                passed1CodeArray[i][3] = codeArray[0][2];
            }

            else if(codeArray[0][0].startsWith(".")){
                i++;
                programCounter = (int) Long.parseLong(codeArray[0][2],16);
                Start = programStart = (int) Long.parseLong(codeArray[0][2],16);
                passed1CodeArray[i][0] = "\t";
                passed1CodeArray[i][1] = codeArray[0][0];
                passed1CodeArray[i][2] = codeArray[0][1];
                passed1CodeArray[i][3] = codeArray[0][2];
            }
            else
            {

                if(isOP(codeArray[i][0]))
                {
                    passed1CodeArray[i][0] = Integer.toHexString(programCounter);
                    passed1CodeArray[i][1] = codeArray[i][0];
                    passed1CodeArray[i][2] = codeArray[i][1];
                    passed1CodeArray[i][3] = codeArray[i][1];
                    programCounter+=3;
                }

                else if(!isOP(codeArray[i][0]))
                {
                    try{
                        passed1CodeArray[i][0] = Integer.toHexString(programCounter);
                        passed1CodeArray[i][1] = codeArray[i][0];
                        passed1CodeArray[i][2] = codeArray[i][1];
						try {
							passed1CodeArray[i][3] = codeArray[i][2];
						} catch (Exception e) {
							passed1CodeArray[i][3] = "\t";
						}
                        inOperand = passed1CodeArray[i][3];


                        switch (passed1CodeArray[i][2]) {
                            case "BASE":
                                //System.out.println("I found base");
                                TempValueCounter=Integer.parseInt(passed1CodeArray[i][0],16);
                                summedCounter = Integer.toHexString(TempValueCounter);
                                passed1CodeArray[i+1][0] = summedCounter;
                                programCounter  = TempValueCounter;
                                break;

                            case "RESW":

                                TempValueCounter=Integer.parseInt(passed1CodeArray[i][0],16)+Integer.parseInt(inOperand)*3;
                                summedCounter = Integer.toHexString(TempValueCounter);
                                passed1CodeArray[i+1][0] = summedCounter;
                                programCounter  = TempValueCounter;
                                break;
                            case "RESB":
								// Todo: Fix the below
                                //Todo: Check the below as you are overwriting your value
                                //bena5od el number el gnbaha ne7awelo l Hex we bengma3o 3la el counter
                                TempValueCounter=Integer.parseInt(passed1CodeArray[i][0],16)+Integer.parseInt(passed1CodeArray[i][2]);
				passed1CodeArray[i+1][1] = Integer.toHexString(TempValueCounter);

                                passed1CodeArray[i+1][0] = summedCounter;
                                programCounter  = TempValueCounter;
                                break;
                            case "WORD":
                                programCounter +=3;
                                break;

                            case "BYTE":
                                String subString = "";
                                int num_bytes;
                                if(passed1CodeArray[i][3].startsWith("x"))
                                {
                                    subString = passed1CodeArray[i][3].substring(2, passed1CodeArray[i][3].length()-1);
                                    num_bytes = subString.length()/2;
                                    programCounter += num_bytes;
                                    if((subString.length()%2)!= 0)
                                    {
                                        programCounter +=1;
                                    }
                                }
                                else if(passed1CodeArray[i][3].startsWith("c"))
                                {
                                    subString = passed1CodeArray[i][3].substring(2, passed1CodeArray[i][3].length()-1);
                                    num_bytes = subString.length();
                                    programCounter += num_bytes;
                                }
                                else{
                                    programCounter += 1;
                                }
                                    break;
                            default:
                                    if(passed1CodeArray[i][2].startsWith("+")){
                                    programCounter +=4;
                                }else{

                                programCounter +=3;
                                break;
                                    }
                        }

                                if(passed1CodeArray[i][2].equalsIgnoreCase("END"))
                                {
                                    passed1CodeArray[i][1] = "blank";
                                    passed1CodeArray[i][2] = codeArray[i][0];
                                    passed1CodeArray[i][3] = codeArray[i][1];
                                    End = (int) Long.parseLong(passed1CodeArray[i][0],16);
                                    End_Address = (int) Long.parseLong(passed1CodeArray[i][0], 16);
                                    Start=(int) Long.parseLong(passed1CodeArray[0][3], 16);
                                    Length1 = End - Start;
                                    System.out.println("PROGRAM LENGTH: "+ End +" - "+Start +"= "+Length1);
                                    Length2 = End_Address - Start;
                                    //System.out.println(Length2);
                                }

                    }
                    catch(NullPointerException exception)
                    {

                    }
                }
            }
        }
        for(int i=0; i<=passed1CodeArray.length-2; i++){
            if(passed1CodeArray[i][0]!="null" ){
//                if(!passed1CodeArray[i][1].contains("null") ){

            SymbolTable[i][0]=passed1CodeArray[i][0];
            SymbolTable[i][1]=passed1CodeArray[i][1];
            System.out.println(passed1CodeArray[i][0]+"\t"+passed1CodeArray[i][1]+"\t"+passed1CodeArray[i][2]);
                //}
            //System.out.println(SymbolTable[i][0]+"\t"+SymbolTable[i][1]);

            //System.out.println(String.valueOf(SymbolTable[0][0]));
            }else {
                i++;
            }
        }
        //modAddress();
    }
                        //------------------------------------------------PASS2------------------------------------------------//


	// nixbpe
	// n, i: immediate or indirect or simple (0,1 or 1,0 or 1,1)
	// x is 1 if the [i][3] has X
	//b, p: base (displacement + address between -2048 and 2047)
	// e format 4 if it has +
    public static void pass2()
    {
        int tempValue = 0;
        for(int j = 0; j < passed1CodeArray.length-1;j++)
        {
            try{
            passed2CodeArray[j][0] = passed1CodeArray[j][0];
            passed2CodeArray[j][1] = passed1CodeArray[j][1];
            passed2CodeArray[j][2] = passed1CodeArray[j][2];
            passed2CodeArray[j][3] = passed1CodeArray[j][3];
            if(!isIndexed(passed2CodeArray[j][3]))
            {
				// handle no object code
                if(
						passed2CodeArray[j][2].equalsIgnoreCase("RESW") ||
                        passed2CodeArray[j][2].equalsIgnoreCase("RESB") ||
						passed2CodeArray[j][2].equalsIgnoreCase("BASE")
				) {
                    passed2CodeArray[j][4] = "NO OBJ CODE";
                }
				// handle word and byte
                else if(passed2CodeArray[j][2].equalsIgnoreCase("WORD")||passed2CodeArray[j][2].equalsIgnoreCase("BYTE"))
                {
                   passed2CodeArray[j][4] = Integer.toHexString(Integer.parseInt(passed2CodeArray[j][3], 10));
                   modAddress2(j);
                }
				// handle formats from 1 to 4
                else{
					// returnOC just gives the format 3/4
                    passed2CodeArray[j][4] = returnOC(passed2CodeArray[j][2]).concat(returnOCp2(passed2CodeArray[j][3]));
                }
            }
            else if(isIndexed(passed2CodeArray[j][3]))
            {


				// nixbpe
				// n, j: #immediate i=1 @indirect n=1, otherwise both are ones
				// x is 1 if the [j][3] has X
				//b, p: base (displacement + address between -2048 and 2047)
				// e format 4 if it has +
				String n, i, x, b, p ,e;
				String op = passed2CodeArray[j][3];
                                String instruction = passed2CodeArray[j][2];
				if(op.startsWith("@")) {
					n = "1";
					i = "0";
				} else if(op.startsWith("#")) {
					n = "0";
					i = "1";
				} else {
					n = "1";
					i = "1";
				}
                                if(op.contains("X")){
                                    x="1";
                                }else{
                                    x="0";
                                    b="0";
                                    p="0";
                                }
                                if(instruction.startsWith("+")){
                                    e="1";
                                }else{
                                    e="0";
                                }
                                //if format 3 or 4 
                                //assume its pc relative  
                                //b=0 and p=1
                                //TA =disp + address is between -2047 and 2048 if not
                                //b=1 and p=0
                                //TA = Base + disp
                                //check TA from symbol table - next instruction location
                                



                String tempCell = "";
                char[] tempCellChars;
                tempCell = returnOC(passed2CodeArray[j][2]).concat(returnOCp2(indexedLabel));
                //System.out.println(tempCell);
                if(tempCell.startsWith("0"))
                {
                    tempCell = "0000"+Integer.toBinaryString(Integer.parseInt(tempCell, 16));
                }else
                {
                    tempCell = Integer.toBinaryString(Integer.parseInt(tempCell, 16));
                }
                //System.out.println(tempCell);
                tempCellChars = tempCell.toCharArray();
                //System.out.println(Arrays.toString(tempCellChars));
                tempCellChars[8] = '1';
                tempCell = String.copyValueOf(tempCellChars);
                //System.out.println("Here"+tempCell);
                if(tempCell.startsWith("0"))
                    tempCell = String.valueOf("0"+Integer.toHexString(Integer.parseInt(tempCell, 2)));
                else
                    tempCell = String.valueOf(Integer.toHexString(Integer.parseInt(tempCell, 2)));

                //System.out.println("Here"+tempCell);
                passed2CodeArray[j][4] = tempCell;
            }
            //System.out.println(passed2CodeArray[j][4]);
            }
            catch(NullPointerException ex)
            {}
        }
        for(int j=0; j<=passed1CodeArray.length-2; j++){

        //System.out.println(passed2CodeArray[i][0]+"\t"+passed2CodeArray[i][1]+"\t"+passed2CodeArray[i][2]+"\t"+passed2CodeArray[i][4]);
        }
    }

    public static void modAddress()
    {
        String newString = "";
        for(int i =1;i<passed2CodeArray.length;i++)
        {
            if(passed1CodeArray[i][0].length()==1)
            {
                newString = "000".concat(passed1CodeArray[i][0]);
                passed1CodeArray[i][0] = newString;
            }
            else if(passed1CodeArray[i][0].length()==2)
            {
                newString = "00".concat(passed1CodeArray[i][0]);
                passed1CodeArray[i][0] = newString;
            }
            else if(passed1CodeArray[i][0].length()==3)
            {
                newString = "0".concat(passed1CodeArray[i][0]);
                passed1CodeArray[i][0] = newString;
            }

        }
    }
    public static void modAddress2(int i)
    {
        String newString = "";
            if(passed2CodeArray[i][4].length()==1)
            {
                newString = "00000".concat(passed2CodeArray[i][4]);
                passed2CodeArray[i][4] = newString;
            }
            else if(passed2CodeArray[i][4].length()==2)
            {
                newString = "0000".concat(passed2CodeArray[i][4]);
                passed2CodeArray[i][4] = newString;
            }
            else if(passed2CodeArray[i][4].length()==3)
            {
                newString = "0".concat(passed2CodeArray[i][4]);
                passed2CodeArray[i][4] = newString;
            }
    }
    public static String returnOCp2(String inString)
    {

        String s = "";
        for(int i = 0; i < passed2CodeArray.length;i++)
        {
            try
                {
                if(passed1CodeArray[i][1].equalsIgnoreCase(inString))
                {
                    s = passed1CodeArray[i][0];
                    System.out.println(i +" "+s);
                    return s;
                }
            }
            catch(NullPointerException exception)
            {}
        }
        return s;
    }

    public static boolean isOP(String opString)
    {
        boolean found = false;
        for(int i = 0; i < opCodeArray.length;i++)
        {
            if(opCodeArray[i][0].equalsIgnoreCase(opString))
            {
                return true;
            }
        }
        return found;
    }

    public static String returnOC(String inOP)
    {
        String opCode = "";

        for(int i = 0; i< opCodeArray.length;i++)
        {
			// check for plus
			boolean hasPlus = inOP.startsWith("+");
			String inOPToBeCompared = inOP;
			// omit plus
			if(hasPlus) {
				inOPToBeCompared = inOPToBeCompared.substring(1);
			}
            if(opCodeArray[i][0].equalsIgnoreCase(inOPToBeCompared))
            {
				//gets the format
                opCode = opCodeArray[i][1];
            }
        }
        return opCode;
    }



//---------------------------------------------------------------------PRINTING---------------------------------------------------------------------------//



    public static void printPass1Table() throws FileNotFoundException
    {
        PrintWriter Pass1File = new PrintWriter("pass1.txt");
        for(int i=0; i<passed1CodeArray.length-1;i++)
        {
			if(passed1CodeArray[i][0] == null)
				passed1CodeArray[i][0] = "0000";
            Pass1File.println(Utils.formatBits(passed1CodeArray[i][0]) + "\t" + passed1CodeArray[i][1] + " \t" + passed1CodeArray[i][2] + " \t" + passed1CodeArray[i][3]);

        }
        System.out.println();

        Pass1File.close();
    }

     public static void printPass2Table() throws FileNotFoundException
    {
        PrintWriter Pass2File = new PrintWriter("pass2.txt");
        for(int i=0; i<passed2CodeArray.length-1;i++)
        {
            Pass2File.println(Utils.formatBits(passed1CodeArray[i][0]) + " \t" +passed2CodeArray[i][1]+" \t"+passed2CodeArray[i][2]+" \t"+passed2CodeArray[i][3]+"  \t  "+passed2CodeArray[i][4]);
        }

        Pass2File.close();
    }

    public static void printHTE() throws FileNotFoundException
    {
        PrintWriter HTE = new PrintWriter("HTE.txt");
        //System.out.println(Length2);
        HTE.println("H."+codeArray[0][0]+"."+Integer.toHexString(Start)+"."+Integer.toHexString(Length2));
        if(passed2CodeArray.length>9)
        {
            int ii = 0;
            HTE.print("T."+Integer.toHexString(Start)+" "+passed2CodeArray[1][0]+" ");
            for(int i =0;i<12;i++)
            {
                if(!passed2CodeArray[i][4].equalsIgnoreCase("NO OBJ CODE"))
                {

                    HTE.print(passed2CodeArray[i][4]);
                    HTE.print(" ");

                }


                ii++;


            }
            Start = Integer.parseInt(passed2CodeArray[ii+2][0],16);
            //System.out.println(Integer.toHexString(Start)+" "+ii);
            Length1 = End_Address - Start;
            HTE.println();
            //System.out.println(Integer.toHexString(Start)+"."+Integer.toHexString(End_Address)+"."+Integer.toHexString(Length));

            HTE.print("T."+passed2CodeArray[ii+2][0]+" "+Integer.toHexString(Length1));
            for(int j = ii;j<16;j++)

            {
                //System.out.println(ii);

               if(!passed2CodeArray[j][4].equalsIgnoreCase("NO OBJ CODE"))
                    HTE.print(" "+passed2CodeArray[j][4]);



            }
        }
        HTE.println();
        HTE.print("END."+Integer.toHexString(End));
        HTE.close();
    }

    public static boolean isIndexed(String inOP)
    {
        boolean isIND = false;
        try{
        for(int i=0; i< inOP.length();i++)
        {
            if(inOP.charAt(i) == ',')
            {
                indexedLabel = inOP.substring(0, i);

                return true;
            }
        }
        }
        catch(NullPointerException e)
        {}
        return isIND;
    }
}
