import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

            int totalbus= 0;
            int totalins = 0;
            int totaldel = 0;


            long tiempototAVLins = 0;
            long tiempototAVLdel = 0;
            long tiempototAVLbus = 0;

            long tiempototBins = 0;
            long tiempototBdel = 0;
            long tiempototBbus = 0;

            long tiempototBplusins = 0;
            long tiempototBplusdel = 0;
            long tiempototBplusbus = 0;

            long tiempototBastins = 0;
            long tiempototBastdel = 0;
            long tiempototBastbus = 0;

            List<Long> AVLInsertTimes = new ArrayList<>();
            List<Long> AVLDeleteTimes = new ArrayList<>();
            List<Long> AVLSearchTimes = new ArrayList<>();


            List<Long> BInsertTimes = new ArrayList<>();
            List<Long> BDeleteTimes = new ArrayList<>();
            List<Long> BSearchTimes = new ArrayList<>();


            List<Long> BPlusInsertTimes = new ArrayList<>();
            List<Long> BPlusDeleteTimes = new ArrayList<>();
            List<Long> BPlusSearchTimes = new ArrayList<>();


            List<Long> BastInsertTimes = new ArrayList<>();
            List<Long> BastDeleteTimes = new ArrayList<>();
            List<Long> BastSearchTimes = new ArrayList<>();
            

            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {

                    if (matcher.group(1) != null){ //Insert
                        starttime = System.currentTimeMillis();
                        LeAVLtree.root = LeAVLtree.insert(LeAVLtree.root, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3))); //AVL
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototAVLins += timediff;
                        totalins++;
                        AVLLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B+
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBplusins += timediff;
                        BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";

                        starttime = System.currentTimeMillis();
                        LeBTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBins += timediff;
                        BLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";
                    
                        starttime = System.currentTimeMillis();
                        LeBStarTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B*
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBastins += timediff;
                        BastLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";
                    
                    
                    }
                    else if (matcher.group(4) != null){ //Delete
                        starttime = System.currentTimeMillis();
                        LeAVLtree.deleteNode(LeAVLtree.root, new Reg(Integer.parseInt(matcher.group(5)), "null")); //AVL
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototAVLdel += timediff;
                        totaldel++;
                        if (LeAVLtree.found == true){
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "Si," + LeAVLtree.foundata.ID + "," + LeAVLtree.foundata.data + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }
                        else{
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "No," + Integer.parseInt(matcher.group(5)) + "," + "N/A" + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.delete(new Reg(Integer.parseInt(matcher.group(5)), null));;//B+
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBplusdel += timediff;
                        if (LeBPlusTree.found == true){
                            BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "Si," + LeBPlusTree.foundata.ID + "," + LeBPlusTree.foundata.data + "\n";
                            LeBPlusTree.found = false;
                            LeBPlusTree.foundata = null;
                        }
                        else{
                            BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "No," + Integer.parseInt(matcher.group(5)) + "," + "N/A" + "\n";
                            LeBPlusTree.found = false;
                            LeBPlusTree.foundata = null;
                        }

                        
                        starttime = System.currentTimeMillis();
                        LeBTree.remove(Integer.parseInt(matcher.group(5)));//B
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBdel += timediff;
                        if (LeBTree.found == true){
                            BLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "Si," + LeBTree.foundata.ID + "," + LeBTree.foundata.data + "\n";
                            LeBTree.found = false;
                            LeBTree.foundata = null;
                        }
                        else{
                            BLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "No," + Integer.parseInt(matcher.group(5)) + "," + "N/A" + "\n";
                            LeBTree.found = false;
                            LeBTree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBStarTree.delete(Integer.parseInt(matcher.group(5)));//B*
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBastdel += timediff;
                        if (LeBStarTree.found == true){
                            BastLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "Si," + LeBStarTree.foundata.ID + "," + LeBStarTree.foundata.data + "\n";
                            LeBStarTree.found = false;
                            LeBStarTree.foundata = null;
                        }
                        else{
                            BastLogs += starttime + "," + finishtime + "," + timediff + ",Eliminar," + "No," + Integer.parseInt(matcher.group(5)) + "," + "N/A" + "\n";
                            LeBStarTree.found = false;
                            LeBStarTree.foundata = null;
                        }

                    }
                    else if (matcher.group(6) != null){ //Search
                        starttime = System.currentTimeMillis();
                        LeAVLtree.search(LeAVLtree.root, Integer.parseInt(matcher.group(7))); //AVL
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototAVLbus += timediff;
                        totalbus++;
                        if (LeAVLtree.found == true){
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "Si," + LeAVLtree.foundata.ID + "," + LeAVLtree.foundata.data + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }
                        else{
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "No," + Integer.parseInt(matcher.group(7)) + "," + "N/A" + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.search(new Reg(Integer.parseInt(matcher.group(7)),null)); //B+ No funciona
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBplusbus += timediff;
                        if (LeBPlusTree.found == true){
                            BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "Si," + LeBPlusTree.foundata.ID + "," + LeBPlusTree.foundata.data + "\n";
                            LeBPlusTree.found = false;
                            LeBPlusTree.foundata = null;
                        }
                        else{
                            BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "No," + Integer.parseInt(matcher.group(7)) + "," + "N/A" + "\n";
                            LeBPlusTree.found = false;
                            LeBPlusTree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBTree.search(Integer.parseInt(matcher.group(7)));//B
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBbus += timediff;
                        if (LeBTree.found == true){
                            BLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "Si," + LeBTree.foundata.ID + "," + LeBTree.foundata.data + "\n";
                            LeBTree.found = false;
                            LeBTree.foundata = null;
                        }
                        else{
                            BLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "No," + Integer.parseInt(matcher.group(7)) + "," + "N/A" + "\n";
                            LeBTree.found = false;
                            LeBTree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBStarTree.search(new Reg(Integer.parseInt(matcher.group(7)), "null"));//B*  
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBastbus += timediff;
                        if (LeBStarTree.found == true){
                            BastLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "Si," + LeBStarTree.foundata.ID + "," + LeBStarTree.foundata.data + "\n";
                            LeBStarTree.found = false;
                            LeBStarTree.foundata = null;
                        }
                        else{
                            BastLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "No," + Integer.parseInt(matcher.group(7)) + "," + "N/A" + "\n";
                            LeBStarTree.found = false;
                            LeBStarTree.foundata = null;
                        }        

                    }
                }   

            }

            LocalDateTime finishtimeEverything = java.time.LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

            System.out.println(finishtimeEverything.toString());
            BufferedWriter writer = new BufferedWriter(new FileWriter("logs\\log-AVL-operaciones-"+finishtimeEverything.format(formatter).toString()+".csv"));
            writer.write(AVLLogs);
            writer.close();
            writer = new BufferedWriter(new FileWriter("logs\\log-B-operaciones-"+finishtimeEverything.format(formatter).toString()+".csv"));
            writer.write(BLogs);
            writer.close();
            writer = new BufferedWriter(new FileWriter("logs\\log-B+-operaciones-"+finishtimeEverything.format(formatter).toString()+".csv"));
            writer.write(BPlusLogs);
            writer.close();
            writer = new BufferedWriter(new FileWriter("logs\\log-Bast-operaciones-"+finishtimeEverything.format(formatter).toString()+".csv"));
            writer.write(BastLogs);
            writer.close();


            System.out.println("AVL tiempos promedios(ms):\nInserción:" + tiempototAVLins/totalins + "\nBúsqueda:" + tiempototAVLbus/totalbus + "\nEliminación:" + tiempototAVLdel/totaldel+"\n\n");
            System.out.println("B tiempos promedios(ms):\nInserción:" + tiempototBins/totalins + "\nBúsqueda:" + tiempototBbus/totalbus + "\nEliminación:" + tiempototBdel/totaldel+"\n\n");
            System.out.println("B+ tiempos promedios(ms):\nInserción:" + tiempototBplusins/totalins + "\nBúsqueda:" + tiempototBplusbus/totalbus + "\nEliminación:" + tiempototBplusdel/totaldel+"\n\n");
            System.out.println("B* tiempos promedios(ms):\nInserción:" + tiempototBastins/totalins + "\nBúsqueda:" + tiempototBastbus/totalbus + "\nEliminación:" + tiempototBastdel/totaldel+"\n\n");

            System.out.println("AVL tiempos totales(ms):\nInserción:" + tiempototAVLins + "\nBúsqueda:" + tiempototAVLbus + "\nEliminación:" + tiempototAVLdel+"\n\n");
            System.out.println("B tiempos totales(ms):\nInserción:" + tiempototBins + "\nBúsqueda:" + tiempototBbus + "\nEliminación:" + tiempototBdel+"\n\n");
            System.out.println("B+ tiempos totales(ms):\nInserción:" + tiempototBplusins + "\nBúsqueda:" + tiempototBplusbus + "\nEliminación:" + tiempototBplusdel+"\n\n");
            System.out.println("B* tiempos totales(ms):\nInserción:" + tiempototBastins + "\nBúsqueda:" + tiempototBastbus + "\nEliminación:" + tiempototBastdel+"\n\n");
        }

    }
}

