import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;

class Node {
    public  Node rightNode;
    public  Node leftNode;
    public int weight;
    public int character;

    public Node(int weight){
        this.weight = weight;
    }
    public Node(int weight,int character){
        this.weight=weight;
        this.character =character;
    }
    public  Node (int weight,Node leftNode,Node rightNode){
        this.weight= weight;
        this.leftNode= leftNode;
        this.rightNode= rightNode;
    }

    public void setRightNode(Node rightNode){
        this.rightNode = rightNode;
    }
     public void setLeftNode(Node leftNode){
        this.leftNode= leftNode;
     }
     public void setWeight(int weight){
        this.weight =weight;
     }
     public void setCharacter(char character){
        this.character= character;
     }
     public Node getRightNode(){
        return this.rightNode;
    }
    public Node getLeftNode(){
        return this.leftNode;
   }
      public int  getCharacter(){
        return this.character;
    }
      public int getWeight(){
        return this.weight ;
}
     public int  compareTo(Node N){
        if(this.weight>N.weight)return 1;
        else if(this.weight<N.weight)
            return -1;
        else return 0;
     }
}

class huffmanTree {
public Node root;
public huffmanTree(List<Node> nodes){
    nodes =new ArrayList<>(nodes);
    sortList(nodes);
    while (nodes.size()>1){
        creatAndReplace(nodes);
    }
    if(nodes.size()==0)
        root=null;
    else
        root = nodes.get(0);
}
    private static void creatAndReplace(List<Node> nodes) {
        Node left = nodes.get(0);
        Node right = nodes.get(1);
        Node parent = new Node(left.weight + right.weight,-1);
        parent.setLeftNode(left);
        parent.setRightNode(right);
        nodes.remove(0);
        nodes.remove(0);
        nodes.add(parent);
        sortList(nodes);
    }

    public static   Comparator<Node> comparator = (o1, o2) -> {
        if(o1.weight>o2.weight) {
            return 1;
        }else
        if(o1.weight==o2.weight){
            return 0;
        }else {
            return -1;
        }
    };
    private static void sortList(List<Node> nodes) {
        Collections.sort(nodes, comparator );
     }

   public  void print(Node root, String string,HashMap map){
        if(root!=null) {
            if (root.getRightNode() == null & root.getLeftNode() == null) {
              // System.out.println(root.character + " corresponding code " + string);
                map.put(root.character, string);
            }
            if (root.getLeftNode() != null) {
                print(root.getLeftNode(), string + "0", map);
            }
            if (root.getRightNode() != null) {
                print(root.getRightNode(), string + "1", map);
            }
        }
   }

}

class Compress {
    static int count = 7;
    static int buffer = 0;
    static int length=0;
    ArrayList<Node> nodes  = new ArrayList<>();
    private File compressFile;
    public Compress(String path, String newPath) throws IOException {
        String out;
        compressFile = new File(path);
                 //This can only have one stream here.
        if(compressFile.isDirectory()){
            out=newPath+".zip";
        }else {
            String prefix = newPath.substring(0, newPath.lastIndexOf("."));//
            out= prefix+".zip";
        }
        BufferedOutputStream outputStream= new BufferedOutputStream(new FileOutputStream(out));
        compressFile(compressFile,outputStream);
        outputStream.close();
    }
    public void compressFile(File ptah,BufferedOutputStream bufferedOutputStream) throws IOException {
        if(ptah.isDirectory()){
            String directoryName = ptah.getPath();
            bufferedOutputStream.write(directoryName.length());
            int type=1;
            bufferedOutputStream.write(type);
            for(int a=0;a<directoryName.length();a++){
                char ch = directoryName.charAt(a);
                bufferedOutputStream.write(ch);
                                 
            }
            compress_file(ptah,bufferedOutputStream);
        }else {
            int type=0;
            String directoryName = ptah.getPath();
           // System.out.println(directoryName+"  "+ directoryName.length());
            bufferedOutputStream.write(directoryName.length());
                         bufferedOutputStream.write(type);//type
            for(int a=0;a<directoryName.length();a++){
                char ch = directoryName.charAt(a);
                bufferedOutputStream.write(ch);
                                
            }
           // System.out.println(directoryName.length() + "   type   "+ type +"   "+ directoryName );
            compress_f(ptah,bufferedOutputStream);
        }
    }

    private void compress_file(File ptah, BufferedOutputStream bufferedOutputStream) throws IOException {
        if (!ptah.exists())
            return;
        File[] files = ptah.listFiles();
         for (int i = 0; i < files.length; i++) {
       compressFile(files[i],bufferedOutputStream);
        }
    }
    private void compress_f(File file, BufferedOutputStream bufferedOutputStream) throws IOException {
        if(!file.exists())
            return;
        int[] characterAndWeight =getInts(file);
        for(int i= 0;i<256;i++){
            if(characterAndWeight[i]!=0){
                nodes.add(new Node(characterAndWeight[i],i));
            }
                 }
        HashMap<Integer,String> map= new HashMap<>();
        huffmanTree huffmanTree =new huffmanTree(nodes);
        huffmanTree.print(huffmanTree.root,"",  map);
        writeFile(file,bufferedOutputStream,map);
        nodes.clear();
        map.clear();
    }

    public static void writeFile(File path, BufferedOutputStream bufferedOutputStream, HashMap<Integer,String> map) throws IOException {
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(path));
        BufferedOutputStream out = bufferedOutputStream;
        String theCodeOfLength="";
        for(int i=0x80000000;i!=0;i>>>=1){
            theCodeOfLength+=(length&i)==0?'0':'1';
        }
        for (int j = 0; j<32 ; j++){
            char ch = theCodeOfLength.charAt(j);
            writeBit(ch-'0',out);
        }
                 //System.out.println(length+" 32-bit encoding "+ yy);
        length=0;

        for(int i =0;i<=255;i++){
            if(map.containsKey(i)){
                String character= map.get(i);
                int a = character.length();
                 out.write((byte)a);
            }else {
                out.write((byte)0);
            }
        }
        for(int i= 0;i<=255;i++){
            if(map.containsKey(i)){
                String character = map.get(i);
                for (int j = 0; j< character.length(); j++) {
                    char ch = character.charAt(j);
                    writeBit(ch-'0',out);
                }
            }
                 }

        //System.out.println(buffer+" the last  code    "+count);
        int  value= fis.read();
        while(value!=-1){
            String str= map.get(value);
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                writeBit(ch-'0',out);
            }
            value = fis.read();
        }
        System.out.println(buffer);
        if(buffer!=0) out.write(buffer);
        buffer=0;
        count=7;
        fis.close();
    }


    private static void writeBit(int ch,BufferedOutputStream outputStream) throws IOException {
        int a= ch<<count;
        buffer=buffer|a;
        count--;
        if (count==-1){
            outputStream.write(buffer);
            count=7;
            buffer=0;
        }
    }



    public  static int[] getInts(File path) throws IOException {
        int [] times = new int[256];
        BufferedInputStream fis = new BufferedInputStream(new  FileInputStream(path));
        int value = fis.read();
        while (value!=-1){
            length++;
            times[value]++;
            value=fis.read();
        }
        fis.close();
        return times;
    }
}
    class decode {
        static  HashMap<String,Integer> hashMap= new HashMap<>();
        static int readCount=7;
        static   ArrayList<Integer> code= new ArrayList<>();
        public  decode(String path, String newPath) throws IOException {
            BufferedInputStream inputStream= new BufferedInputStream(new FileInputStream(path));
            File file= new File(path);
            decodeFile(inputStream, newPath);
            inputStream.close();
        }

        private void decodeFile(BufferedInputStream inputStream, String newPath) throws IOException {
            int lengthOfFileName = inputStream.read();
            int fileType= inputStream.read();
            String name="" ;
            while(name.length()<lengthOfFileName){
                int value= inputStream.read();
                name= name+(char)value;
            }
            if(fileType==1){
                File file1= new File(name);
                         // System.out.println(name+" folder");//folder
                if(!file1.exists())
                     file1.mkdir();
                decodeFile(inputStream, newPath);
            }else if(fileType==0) {
                             decode_file(inputStream,name, newPath);//Unzip the file
            }else {
                // it decompress the file by order ,if the type isn~t one or zero ,it means that it reaches the end of the file
                //System.exit(0);
            }

        }

        private void decode_file(BufferedInputStream inputStream, String str, String newPath) throws IOException {
            BufferedOutputStream outputStream =new BufferedOutputStream(new FileOutputStream(newPath));
            int[] str_length= new int [256];

            int value;
            int bb=24;
            int lengthOfChar=0;
            for(int j=0;j<4;j++){
                value=inputStream.read();
                           // System.out.println("32-bit encoding:"+value);
                int tt=value<<bb;
                bb=bb-8;
                lengthOfChar=lengthOfChar|tt;
            }
                 // length=yy;//read the length of the character

                 // System.out.println(yy+"length");///////////////
            //System.out.println(inputStream.available());

            for (int i=0;i<256;i++){
                value= inputStream.read();
                str_length[i]=value;
                     } 

            for(int j =0;j<256;j++) {
                String s = "";
                if (str_length[j] != 0) {
                    int x=0;
                    while(x<str_length[j]){
                        if(code.size()==0){
                            value=inputStream.read();
                            read(value);
                        }
                        s=s+code.get(0);
                        code.remove(0);
                        x++;
                    }
                    hashMap.put(s, j);
                }
                     }//Build a hash table

                   // System.out.println( "code size after constructing the hash table" + code.size());



            int written_length =0;
            String theCodeOfRead="";
            while (written_length<lengthOfChar){
                if(code.size()==0){
                    value=inputStream.read();
                    read(value);
                }
                theCodeOfRead=theCodeOfRead+code.get(0);
                code.remove(0);
                if(hashMap.containsKey(theCodeOfRead)){
                   /// System.out.println(ss+ "   code  ");
                    outputStream.write(hashMap.get(theCodeOfRead));
                    written_length++;
                    theCodeOfRead="";
                }

            }
            code.clear();
            hashMap.clear();
            outputStream.close();
            decodeFile(inputStream, newPath);

        }
        private static void read(int x ){
            for(int i=0;i<8;i++){
                int y=x>> readCount;
                readCount--;
                if(readCount==-1)
                    readCount=7;
                y= y&1;
                code.add(y);
            }
        }
}
    class Main {
	//Galvenā klase programmas kontrolei
	 static Scanner sc = new Scanner(System.in);
	 public static void main(String[] args){
	   boolean isGoing = true;
	   int input = 0;
	   while(isGoing){
	     System.out.println("Input command number\n1: comp\n2: decomp\n3: size\n4: equal\n5: about\n6: exit");
	     if(sc.hasNextInt()){
	       input = sc.nextInt();
	       switch(input){
	         case 1:
	           compress();
	           break;
	         case 2:
	           decompress();
	           break;
	         case 3:
	           size();
	           break;
	         case 4:
	           equal();
	           break;
	         case 5:
	           about();
	           break;
	         case 6:
	           isGoing = exit();
	           break;
	         default:
	           System.out.println("Input-error");
	           break;
	       }
	     }
	     else{
	       System.out.println("Input-error");
	     }
	   }
	 }


	 // Faila saspiešana
	 public static void compress(){
	   //izvada paziņojumu
	   
	   System.out.println("source file name:"); 
	   String fileName = "";
	   String archiveFileName = "";
	   if(sc.hasNext()){
	     fileName = sc.next();
	   }
	   else{
	     System.out.println("Input-error");
	   }
	   System.out.println("archive name:"); 
	   if(sc.hasNext()){
	     archiveFileName = sc.next();
	   }
	   else{
	     System.out.println("Input-error");
	   }
	   try {
		Compress compress = new Compress(fileName, archiveFileName);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	 }

	 // Faila atkodēšana
	 public static void decompress(){
	   System.out.println("archive name:"); 
	   String fileName = "";
	   String archiveFileName = "";
	  
	   if(sc.hasNext()){
	     archiveFileName = sc.next();
	   }
	   else{
	     System.out.println("Input-error");
	   }
	    System.out.println("file name:"); 
	   if(sc.hasNext()){
	     fileName = sc.next();
	   }
	   else{
	     System.out.println("Input-error");
	   }
	   try {
		decode decompress = new decode(archiveFileName, fileName);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	 }

	 // Izvada faila izmēru
	 public static void size(){
	   System.out.println("file name:"); 
	   String fileName = "";
	   if(sc.hasNext()){
	     fileName = sc.next();
	   }
	   else{
	     System.out.println("Input-error");
	   }
	   File file = new File(fileName);
	   if (!file.exists() || !file.isFile()) {System.out.println("File doesn't exist"); return;}
	   System.out.println("size:" + file.length() + " bytes");  

	 }

	 //salīdzina divu failu izmērus
	 public static void equal(){
		 long firstLength = 0;
		 long secondLength = 0;
	   System.out.println("first file name:"); 
	   String firstFileName = "";
	   if(sc.hasNext()){
	     firstFileName = sc.next();
	     File file = new File(firstFileName);
	     if (!file.exists() || !file.isFile()) { System.out.println("File doesn't exist"); return;}
	     firstLength = file.length();  
	   }
	   else{
	     System.out.println("Input-error");
	   }

	   System.out.println("second file name:"); 
	   String secondFileName = "";
	   if(sc.hasNext()){
	     secondFileName = sc.next();
	     File secondFile = new File(firstFileName);
	     if (!secondFile.exists() || !secondFile.isFile()) {System.out.println("File doesn't exist"); return;}
	     secondLength = secondFile.length();
	     System.out.println(firstLength == secondLength);
	   }
	   else{
	     System.out.println("Input-error");
	   }
	 }
	 // Metode programmas izslēgšanai
	 public static void about(){
	   System.out.println("201RDB153 Maksims Fedosejenkovs 9.grupa\n" + 
	   		"201RDB340 Roberts Zande 9.grupa\n" + 
	   		"201RDB170 Antons Lohovs 9.grupa\n" + 
	   		"201RDB409 Danils Kubiškins 9.grupa\n" + 
	   		"201RDB343 Vladislavs Judins 9.grupa\n");
	 }
	 // Metode programmas izslēgšanai
	 public static boolean exit(){
	   return false;
	 }
	}