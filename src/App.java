import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class App {
    public static void main(String[] args) throws Exception {
        
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\Dany\\Downloads\\operaciones.txt"))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            String patternString = "(Insert):\\{id:(\\d+),nombre:\"([^\"]+)\"\\}" + "|(Delete):\\{id:(\\d+)\\}" + "|(Search):\\{id:(\\d+)\\}";
            Pattern pattern = Pattern.compile(patternString);

            System.out.println("Por favor ingresar el grado del árbol");
            int degree = Integer.parseInt(reader.readLine()); 
            BPlusTree LeBPlusTree = new BPlusTree(degree);
            AVLTree LeAVLtree = new AVLTree();
            BTree LeBTree = new BTree(degree);
            BStarTree LeBStarTree = new BStarTree(degree);

            String AVLLogs = "Hora de inicio(ms),Hora de fin(ms),Tiempo de operacion(ms),Operacion,Encontrado,Id,Nombre"+ "\n";
            String BLogs = "Hora de inicio(ms),Hora de fin(ms),Tiempo de operacion(ms),Operacion,Encontrado,Id,Nombre"+ "\n";
            String BPlusLogs = "Hora de inicio(ms),Hora de fin(ms),Tiempo de operacion(ms),Operacion,Encontrado,Id,Nombre"+ "\n";
            String BastLogs = "Hora de inicio(ms),Hora de fin(ms),Tiempo de operacion(ms),Operacion,Encontrado,Id,Nombre"+ "\n";
            long starttime;
            long finishtime;
            long timediff;


            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {

                    if (matcher.group(1) != null){ //Insert
                        starttime = System.currentTimeMillis();
                        LeAVLtree.root = LeAVLtree.insert(LeAVLtree.root, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3))); //AVL
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        AVLLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B+
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";

                        starttime = System.currentTimeMillis();
                        LeBTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        BLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";
                    
                        starttime = System.currentTimeMillis();
                        LeBStarTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B*
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        BastLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";
                    
                    
                    }
                    else if (matcher.group(4) != null){ //Delete
                        starttime = System.currentTimeMillis();
                        LeAVLtree.deleteNode(LeAVLtree.root, new Reg(Integer.parseInt(matcher.group(5)), "null")); //AVL
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        if (LeAVLtree.found == true){
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "Si," + LeAVLtree.foundata.ID + "," + LeAVLtree.foundata.name + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }
                        else{
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "No," + Integer.parseInt(matcher.group(5)) + "," + "N/A" + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.delete(Integer.parseInt(matcher.group(5)));//B+
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        

                        LeBTree.remove(Integer.parseInt(matcher.group(5)));//B
                        LeBStarTree.delete(Integer.parseInt(matcher.group(5)));//B*

                    }
                    else if (matcher.group(6) != null){ //Search
                        LeAVLtree.search(LeAVLtree.root, Integer.parseInt(matcher.group(7))); //AVL
                        LeBPlusTree.search(Integer.parseInt(matcher.group(7))); //B+
                        LeBTree.search(Integer.parseInt(matcher.group(7)));//B   
                        //LeBStarTree.search(new Reg(Integer.parseInt(matcher.group(7)), "null"));//B*          

                    }
                }   

            }

            LocalDateTime finishtimeEverything = java.time.LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

            System.out.println(finishtimeEverything.toString());
            BufferedWriter writer = new BufferedWriter(new FileWriter("logs\\log-AVL-operaciones-"+finishtimeEverything.format(formatter).toString()+ ".csv"));
            writer.write(AVLLogs);
            writer.close();
            writer = new BufferedWriter(new FileWriter("logs\\log-B-operaciones-"+finishtimeEverything.format(formatter).toString()+ ".csv"));
            writer.write(BLogs);
            writer.close();
            writer = new BufferedWriter(new FileWriter("logs\\log-B+-operaciones-"+finishtimeEverything.format(formatter).toString()+ ".csv"));
            writer.write(BPlusLogs);
            writer.close();
            writer = new BufferedWriter(new FileWriter("logs\\log-Bast-operaciones-"+finishtimeEverything.format(formatter).toString()+ ".csv"));
            writer.write(BastLogs);
            writer.close();
        }

        






        long starttime = System.nanoTime();


        long finishtime = System.nanoTime();

        long timediff = finishtime-starttime;

        System.out.println("timediff in ns = " + timediff);



    }
}

