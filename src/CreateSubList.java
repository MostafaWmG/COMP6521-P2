import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class CreateSubList {

	FileInputStream fileIn;
	
	String inpfileNameWithPath;
	public String getInpfileNameWithPath() {
		return inpfileNameWithPath;
	}


	public void setInpfileNameWithPath(String inpfileNameWithPath) {
		this.inpfileNameWithPath = inpfileNameWithPath;
	}



	String outfilePath;
	String inpfileName;
	int tupleSize;
	int readIO=0;
	int maxFileSize=2000000;
	public int getMaxFileSize() {
		return maxFileSize;
	}


	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}


	public int getReadIO() {
		return readIO;
	}


	public void setReadIO(int readIO) {
		this.readIO = readIO;
	}



	int recordNumber=0;
	
	public int getRecordNumber() {
		return recordNumber;
	}


	public CreateSubList(String inpfileNameWithPath,
			                  String outFilePath){
		try {
			
			this.inpfileNameWithPath=inpfileNameWithPath;
			this.outfilePath=outFilePath;
			fileIn=new FileInputStream(this.inpfileNameWithPath);
			inpfileName=inpfileNameWithPath.substring(inpfileNameWithPath.lastIndexOf("/")+1);
			this.tupleSize=tupleSize;
			
			RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
			List<String> jvmArgs =  runtimeMXBean.getInputArguments();
			if(jvmArgs.contains("-Xmx5m"))
			{
				setMaxFileSize(1800000);
			}else if(jvmArgs.contains("-Xmx10m")){
				setMaxFileSize(2500000);
			}else{
				setMaxFileSize(2000000);
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public ArrayList<String> readFile(int tupleSize,Comparator<String> cmptr){
		
		ArrayList<String> arrLst=new ArrayList<String>();
		
		int fileSize=0;
		int tmpSize=0; 		
		int fileNumber=0;
		
				
		int blockSize=4000;				
		
		Scanner fileScanner= new Scanner(fileIn);
		try{
			
			while(fileScanner.hasNextLine()){
				
				arrLst.addAll(getBlock(fileScanner,tupleSize));
				readIO+=1;
				
				tmpSize+=tupleSize;
				fileSize+=blockSize;
				
//				maxFileSize
				
				if(fileSize>=maxFileSize || !(fileScanner.hasNextLine())){				
					Collections.sort(arrLst, cmptr);
					fileNumber+=1;
					writeToFile(inpfileName,fileNumber,arrLst);
					recordNumber=arrLst.size()+recordNumber;
					arrLst.clear();
					fileSize=0;	
				}	
			}
			
		}catch(Exception e){			
			e.printStackTrace();			
		}
		fileScanner.close();
		System.out.println("Number Of I/O to create sublists of " + getInpfileNameWithPath() + ":" + (2* getReadIO()));
		System.out.println("--------------------------------------------------------");
		return arrLst;
	}
	
	private ArrayList<String> getBlock(Scanner fileScanner,int maxLineSize){
		
//		int sizeInByte=4000;
		int tupleNum= 0;
		
		switch (maxLineSize){
			case 100:
				tupleNum=40;
				break;
			case 60:
				tupleNum=60;
				break;
			default:
				tupleNum=40;
				break;					
		}
		
		ArrayList<String> arrTmp=new ArrayList<String>();
		String tmpLine;
		
		for(int i=1;i<=tupleNum && fileScanner.hasNextLine();i++){
			tmpLine=fileScanner.nextLine();
			arrTmp.add(tmpLine);
		}
		
		return arrTmp;
	}
	
	private void writeToFile(String filename,int prefix,ArrayList<String> arrLst){
		
		String tmpfileNameWithPath=this.outfilePath + "//" + prefix + "-" + filename;
		try {
			PrintWriter pw= new PrintWriter(tmpfileNameWithPath);
			for(String str:arrLst){
				pw.println(str);
			}
			
			pw.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		
		int empRecordNumber=0;
		int prjRecordNumber=0;

		String inputFileDirectory="G:/ADBInputFiles/";
		String outputFileDirectory="G:/ADBOutputFiles/";
		
		String empInFile="Employee.txt";
		String empOutFile="Emp-out.txt";
		
		String prjInFile="Project.txt";
		String prjOutFile="prj-out.txt";
		
		String joinInFile="JoinFile-in.txt";

		
		String sublistFileDirectoryEmp="G:/sublist1";		
		String sublistFileDirectoryPrj="G:/sublist2";

		long BeginTimeEmp=0;
		long EndTimeEmp=0;
		long BeginTime=0;
				
		long BeginTimePrj=0;
		long EndTimePrj=0;
		
		long BeginTimeEmpMerge=0;
		long EndTimeEmpMerge=0;
		
		long BeginTimePrjMerge=0;
		long EndTimePrjMerge=0;
		
		long BeginTimeJoin=0;
		long EndTimeJoin=0;
		
		long BeginTotalSortTime=0;
		long EndTotalSortTime=0;
		
		long BeginTotalSortJoinTime=0;
		long EndTotalSortJoinTime=0;
		JoinFiles jf=null;
		
		try {	
		// TODO Auto-generated method stub
			     
		MakeDirectory mDir=new MakeDirectory(sublistFileDirectoryEmp);
		mDir.checkDirectory();
		
		mDir.setFolderName(sublistFileDirectoryPrj);
		mDir.checkDirectory();
		
		mDir.setFolderName(outputFileDirectory);
		mDir.checkDirectory();
		
		System.out.println("Press Enter To Start");
		Scanner scnr= new Scanner(System.in);
		String enter = scnr.nextLine();
		BeginTime = System.currentTimeMillis();
		
		BeginTotalSortJoinTime= System.currentTimeMillis();
		
		BeginTotalSortTime= System.currentTimeMillis();
		
		BeginTimeEmp = System.currentTimeMillis();
		
		CreateSubList csbEmp=new CreateSubList(inputFileDirectory+empInFile, sublistFileDirectoryEmp);		
		csbEmp.readFile(100,new PrimaryKeyComparatorEmp());
		empRecordNumber= csbEmp.getRecordNumber()-1;
		
		EndTimeEmp = System.currentTimeMillis();

		
		BeginTimePrj = System.currentTimeMillis();
		
		CreateSubList csbPrj=new CreateSubList(inputFileDirectory+prjInFile, sublistFileDirectoryPrj);
		csbPrj.readFile(60,new PrimaryKeyComparatorPrj());		
		prjRecordNumber= csbPrj.getRecordNumber()-1;
		
		EndTimePrj = System.currentTimeMillis();
		
		
		MergeSubList mg=new MergeSubList();
		BeginTimeEmpMerge = System.currentTimeMillis();
		int i =mg.merge(empRecordNumber, outputFileDirectory ,empOutFile, sublistFileDirectoryEmp,1);
		EndTimeEmpMerge = System.currentTimeMillis();
		
		BeginTimePrjMerge = System.currentTimeMillis();
		int j =mg.merge(prjRecordNumber, outputFileDirectory ,prjOutFile, sublistFileDirectoryPrj,2);
		EndTimePrjMerge = System.currentTimeMillis();
		
		EndTotalSortTime= System.currentTimeMillis();
		
		BeginTimeJoin = System.currentTimeMillis();
		jf= new JoinFiles(outputFileDirectory+empOutFile, 
									outputFileDirectory+prjOutFile, 
									outputFileDirectory+joinInFile);
		jf.join();
		EndTimeJoin = System.currentTimeMillis();						
		
		EndTotalSortJoinTime= System.currentTimeMillis();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Create subLists Employee Time(ms):"+ (EndTimeEmp - BeginTimeEmp));
		System.out.println("--------------------------------------------------------");
		System.out.println("Create subLists Project Time(ms):"+ (EndTimePrj - BeginTimePrj));
		System.out.println("--------------------------------------------------------");
		System.out.println("Merge Employee Time(ms):"+ (EndTimeEmpMerge - BeginTimeEmpMerge));
		System.out.println("--------------------------------------------------------");
		System.out.println("Merge Project Time(ms):"+ (EndTimePrjMerge - BeginTimePrjMerge));
		System.out.println("--------------------------------------------------------");
		System.out.println("Join Relations Time(ms):"+ (EndTimeJoin - BeginTimeJoin));
		System.out.println("");	
		System.out.println("Number Tuples In Join File : " + jf.getNumJoinLine());
		System.out.println("--------------------------------------------------------");
		
		System.out.println("--------------------------------------------------------");
		System.out.print("Total sort Time(minute):");
		System.out.printf("%.2f", ((float)(EndTotalSortTime-BeginTotalSortTime)/(float)(1000*60)));
		System.out.println();
		System.out.println("--------------------------------------------------------");
		System.out.print("Total sort Time(ms):");
		System.out.println((EndTotalSortTime-BeginTotalSortTime));
		
		System.out.println("--------------------------------------------------------");
		System.out.print("Total sort plus join Time(second):");
		System.out.printf("%.4f" ,((float)(EndTotalSortJoinTime-BeginTotalSortJoinTime)/(float)(1000)));
		System.out.println();
		System.out.println("--------------------------------------------------------");
		long endTime = System.currentTimeMillis();		
		System.out.println(("Total Execution Time(ms):" + (endTime-BeginTime)));
	}
	

}
