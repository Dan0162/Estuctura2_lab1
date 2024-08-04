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
import java.util.Comparator;



public class App {
    public static class Pair {
        long time;
        Reg reg;

        Pair(long time, Reg reg) {
            this.time = time;
            this.reg = reg;
        }

        @Override
        public String toString() {
            return "\n[id: " + reg.ID + ", name: " + reg.data + "]Time: " + time + "ms";
        }
    }

    // Function to update top 10 times
    private static void updateTop10(List<Pair> top10List, long time, Reg reg) {
        Pair pair = new Pair(time, reg);
        if (top10List.size() < 10) {
            top10List.add(pair);
        } else if (time < top10List.get(9).time) {
            top10List.set(9, pair);
        }
        top10List.sort(Comparator.comparingLong(p -> p.time));
    }

    private static void updateTop10Slowest(List<Pair> top10List, long time, Reg reg) {
        Pair pair = new Pair(time, reg);
        if (top10List.size() < 10) {
            top10List.add(pair);
        } else if (time > top10List.get(9).time) {
            top10List.set(9, pair);
        }
        top10List.sort(Comparator.comparingLong((Pair p) -> p.time).reversed());
    }

    
    public static void main(String[] args) throws Exception {
        
        
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src\\operaciones.txt"))) {
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

            List<Pair> Top10BestInsAVL = new ArrayList<>();
            List<Pair> Top10BestInsB = new ArrayList<>();
            List<Pair> Top10BestInsBPlus = new ArrayList<>();
            List<Pair> Top10BestInsBAst = new ArrayList<>();

            List<Pair> Top10BestDelAVL = new ArrayList<>();
            List<Pair> Top10BestDelB = new ArrayList<>();
            List<Pair> Top10BestDelBPlus = new ArrayList<>();
            List<Pair> Top10BestDelBAst = new ArrayList<>();

            List<Pair> Top10BestBusAVL = new ArrayList<>();
            List<Pair> Top10BestBusB = new ArrayList<>();
            List<Pair> Top10BestBusBPlus = new ArrayList<>();
            List<Pair> Top10BestBusBAst = new ArrayList<>();


            List<Pair> Top10SlowestInsAVL = new ArrayList<>();
            List<Pair> Top10SlowestInsB = new ArrayList<>();
            List<Pair> Top10SlowestInsBPlus = new ArrayList<>();
            List<Pair> Top10SlowestInsBAst = new ArrayList<>();

            List<Pair> Top10SlowestDelAVL = new ArrayList<>();
            List<Pair> Top10SlowestDelB = new ArrayList<>();
            List<Pair> Top10SlowestDelBPlus = new ArrayList<>();
            List<Pair> Top10SlowestDelBAst = new ArrayList<>();

            List<Pair> Top10SlowestBusAVL = new ArrayList<>();
            List<Pair> Top10SlowestBusB = new ArrayList<>();
            List<Pair> Top10SlowestBusBPlus = new ArrayList<>();
            List<Pair> Top10SlowestBusBAst = new ArrayList<>();

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
                        updateTop10(Top10BestInsAVL, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));
                        updateTop10Slowest(Top10SlowestInsAVL, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));
                        AVLLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B+
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBplusins += timediff;
                        updateTop10(Top10BestInsBPlus, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));
                        updateTop10Slowest(Top10SlowestInsBPlus, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3))); 
                        BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";

                        starttime = System.currentTimeMillis();
                        LeBTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBins += timediff;
                        updateTop10(Top10BestInsB, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3))); 
                        updateTop10Slowest(Top10SlowestInsB, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));
                        BLogs += starttime + "," + finishtime + "," + timediff + ",Insertar," + "N/A," + Integer.parseInt(matcher.group(2)) + "," + matcher.group(3) + "\n";
                    
                        starttime = System.currentTimeMillis();
                        LeBStarTree.insert(new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));//B*
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBastins += timediff;
                        updateTop10(Top10BestInsBAst, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3))); 
                        updateTop10Slowest(Top10SlowestInsBAst, timediff, new Reg(Integer.parseInt(matcher.group(2)), matcher.group(3)));
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
                            updateTop10(Top10BestDelAVL, timediff, new Reg(LeAVLtree.foundata.ID, LeAVLtree.foundata.data));
                            updateTop10Slowest(Top10SlowestDelAVL, timediff, new Reg(LeAVLtree.foundata.ID, LeAVLtree.foundata.data));
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
                            updateTop10(Top10BestDelBPlus, timediff, new Reg(LeBPlusTree.foundata.ID, LeBPlusTree.foundata.data));
                            updateTop10Slowest(Top10SlowestDelBPlus, timediff, new Reg(LeBPlusTree.foundata.ID, LeBPlusTree.foundata.data));
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
                            updateTop10(Top10BestDelB, timediff, new Reg(LeBTree.foundata.ID, LeBTree.foundata.data));
                            updateTop10Slowest(Top10SlowestDelB, timediff, new Reg(LeBTree.foundata.ID, LeBTree.foundata.data));
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
                            updateTop10(Top10BestDelBAst, timediff, new Reg(LeBStarTree.foundata.ID, LeBStarTree.foundata.data));
                            updateTop10Slowest(Top10SlowestDelBAst, timediff, new Reg(LeBStarTree.foundata.ID, LeBStarTree.foundata.data));
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
                            updateTop10(Top10BestBusAVL, timediff, new Reg(LeAVLtree.foundata.ID, LeAVLtree.foundata.data));
                            updateTop10Slowest(Top10SlowestBusAVL, timediff, new Reg(LeAVLtree.foundata.ID, LeAVLtree.foundata.data));
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }
                        else{
                            AVLLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "No," + Integer.parseInt(matcher.group(7)) + "," + "N/A" + "\n";
                            LeAVLtree.found = false;
                            LeAVLtree.foundata = null;
                        }

                        starttime = System.currentTimeMillis();
                        LeBPlusTree.search(new Reg(Integer.parseInt(matcher.group(7)),null)); //B+
                        finishtime = System.currentTimeMillis();
                        timediff = finishtime - starttime;
                        tiempototBplusbus += timediff;
                        if (LeBPlusTree.found == true){
                            BPlusLogs += starttime + "," + finishtime + "," + timediff + ",Buscar," + "Si," + LeBPlusTree.foundata.ID + "," + LeBPlusTree.foundata.data + "\n";
                            updateTop10(Top10BestBusBPlus, timediff, new Reg(LeBPlusTree.foundata.ID, LeBPlusTree.foundata.data));
                            updateTop10Slowest(Top10SlowestBusBPlus, timediff, new Reg(LeBPlusTree.foundata.ID, LeBPlusTree.foundata.data));
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
                            updateTop10(Top10BestBusB, timediff, new Reg(LeBTree.foundata.ID, LeBTree.foundata.data));
                            updateTop10Slowest(Top10SlowestBusB, timediff, new Reg(LeBTree.foundata.ID, LeBTree.foundata.data));
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
                            updateTop10(Top10BestBusBAst, timediff, new Reg(LeBStarTree.foundata.ID, LeBStarTree.foundata.data));
                            updateTop10Slowest(Top10SlowestBusBAst, timediff, new Reg(LeBStarTree.foundata.ID, LeBStarTree.foundata.data));
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
            writer = new BufferedWriter(new FileWriter("logs\\log-estadisticas-"+finishtimeEverything.format(formatter).toString()+".txt"));

            writer.write("AVL tiempos promedios(ms):\nInserción:" + tiempototAVLins/totalins + "\nBúsqueda:" + tiempototAVLbus/totalbus + "\nEliminación:" + tiempototAVLdel/totaldel+"\n\n");
            writer.write("B tiempos promedios(ms):\nInserción:" + tiempototBins/totalins + "\nBúsqueda:" + tiempototBbus/totalbus + "\nEliminación:" + tiempototBdel/totaldel+"\n\n");
            writer.write("B+ tiempos promedios(ms):\nInserción:" + tiempototBplusins/totalins + "\nBúsqueda:" + tiempototBplusbus/totalbus + "\nEliminación:" + tiempototBplusdel/totaldel+"\n\n");
            writer.write("B* tiempos promedios(ms):\nInserción:" + tiempototBastins/totalins + "\nBúsqueda:" + tiempototBastbus/totalbus + "\nEliminación:" + tiempototBastdel/totaldel+"\n\n");
            
            writer.write("\nTop 10 Inserciones AVL(ms):" + Top10BestInsAVL.toString()+"\n" + "\nTop 10 Búsquedas AVL(ms):" + Top10BestBusAVL.toString()+"\n" + "\nTop 10 EliminacionesAVL(ms):" + Top10BestDelAVL.toString()+"\n");
            writer.write("\nTop 10 peores Inserciones AVL(ms):" + Top10SlowestInsAVL.toString()+"\n" + "\nTop 10 peores Búsquedas AVL(ms):" + Top10SlowestBusAVL.toString()+"\n" + "\nTop 10 peores EliminacionesAVL(ms):" + Top10BestDelAVL.toString()+"\n\n");
            
            writer.write("\nTop 10 Inserciones B(ms):" + Top10BestInsB.toString()+"\n" + "\nTop 10 Búsquedas B(ms):" + Top10BestBusB.toString()+"\n" + "\nTop 10 EliminacionesB(ms):" + Top10BestDelB.toString()+"\n");
            writer.write("\nTop 10 peores Inserciones B(ms):" + Top10SlowestInsB.toString()+"\n" + "\nTop 10 peores Búsquedas B(ms):" + Top10SlowestBusB.toString()+"\n" + "\nTop 10 peores EliminacionesB(ms):" + Top10BestDelB.toString()+"\n\n");
            
            writer.write("\nTop 10 Inserciones BPlus(ms):" + Top10BestInsBPlus.toString()+"\n" + "\nTop 10 Búsquedas BPlus(ms):" + Top10BestBusBPlus.toString()+"\n" + "\nTop 10 EliminacionesBPlus(ms):" + Top10BestDelBPlus.toString()+"\n");
            writer.write("\nTop 10 peores Inserciones BPlus(ms):" + Top10SlowestInsBPlus.toString()+"\n" + "\nTop 10 peores Búsquedas BPlus(ms):" + Top10SlowestBusBPlus.toString()+"\n" + "\nTop 10 peores EliminacionesBPlus(ms):" + Top10BestDelBPlus.toString()+"\n\n");

            writer.write("\nTop 10 Inserciones BAst(ms):" + Top10BestInsBAst.toString()+"\n" + "\nTop 10 Búsquedas BAst(ms):" + Top10BestBusBAst.toString()+"\n" + "\nTop 10 EliminacionesBAst(ms):" + Top10BestDelBAst.toString()+"\n");
            writer.write("\nTop 10 peores Inserciones BAst(ms):" + Top10SlowestInsBAst.toString()+"\n" + "\nTop 10 peores Búsquedas BAst(ms):" + Top10SlowestBusBAst.toString()+"\n" + "\nTop 10 peores EliminacionesBAst(ms):" + Top10BestDelBAst.toString());
            writer.close();

            System.out.println("AVL tiempos promedios(ms):\nInserción:" + tiempototAVLins/totalins + "\nBúsqueda:" + tiempototAVLbus/totalbus + "\nEliminación:" + tiempototAVLdel/totaldel+"\n\n");
            System.out.println("B tiempos promedios(ms):\nInserción:" + tiempototBins/totalins + "\nBúsqueda:" + tiempototBbus/totalbus + "\nEliminación:" + tiempototBdel/totaldel+"\n\n");
            System.out.println("B+ tiempos promedios(ms):\nInserción:" + tiempototBplusins/totalins + "\nBúsqueda:" + tiempototBplusbus/totalbus + "\nEliminación:" + tiempototBplusdel/totaldel+"\n\n");
            System.out.println("B* tiempos promedios(ms):\nInserción:" + tiempototBastins/totalins + "\nBúsqueda:" + tiempototBastbus/totalbus + "\nEliminación:" + tiempototBastdel/totaldel+"\n\n");

            System.out.println("AVL tiempos totales(ms):\nInserción:" + tiempototAVLins + "\nBúsqueda:" + tiempototAVLbus + "\nEliminación:" + tiempototAVLdel+"\n\n");
            System.out.println("B tiempos totales(ms):\nInserción:" + tiempototBins + "\nBúsqueda:" + tiempototBbus + "\nEliminación:" + tiempototBdel+"\n\n");
            System.out.println("B+ tiempos totales(ms):\nInserción:" + tiempototBplusins + "\nBúsqueda:" + tiempototBplusbus + "\nEliminación:" + tiempototBplusdel+"\n\n");
            System.out.println("B* tiempos totales(ms):\nInserción:" + tiempototBastins + "\nBúsqueda:" + tiempototBastbus + "\nEliminación:" + tiempototBastdel+"\n\n");
       
            System.out.println("Top 10 Inserciones AVL(ms):" + Top10BestInsAVL.toString()+"\n");
            System.out.println("Top 10 Inserciones B(ms):" + Top10BestInsB.toString()+"\n");
            System.out.println("Top 10 Inserciones BPlus(ms):" + Top10BestInsBPlus.toString()+"\n");
            System.out.println("Top 10 Inserciones BAst(ms):" + Top10BestInsBAst.toString()+"\n\n");

            System.out.println("Top 10 Búsquedas AVL(ms):" + Top10BestBusAVL.toString()+"\n");
            System.out.println("Top 10 Búsquedas B(ms):" + Top10BestBusB.toString()+"\n");
            System.out.println("Top 10 Búsquedas BPlus(ms):" + Top10BestBusBPlus.toString()+"\n");
            System.out.println("Top 10 Búsquedas BAst(ms):" + Top10BestBusBAst.toString()+"\n\n");

            System.out.println("Top 10 Eliminaciones AVL(ms):" + Top10BestDelAVL.toString()+"\n");
            System.out.println("Top 10 Eliminaciones B(ms):" + Top10BestDelB.toString()+"\n");
            System.out.println("Top 10 Eliminaciones BPlus(ms):" + Top10BestDelBPlus.toString()+"\n");
            System.out.println("Top 10 Eliminaciones BAst(ms):" + Top10BestDelBAst.toString()+"\n");

            System.out.println("Top 10 peores Inserciones AVL(ms):" + Top10SlowestInsAVL.toString()+"\n");
            System.out.println("Top 10 peores Inserciones B(ms):" + Top10SlowestInsB.toString()+"\n");
            System.out.println("Top 10 peores Inserciones BPlus(ms):" + Top10SlowestInsBPlus.toString()+"\n");
            System.out.println("Top 10 peores Inserciones BAst(ms):" + Top10SlowestInsBAst.toString()+"\n\n");

            System.out.println("Top 10 peores Búsquedas AVL(ms):" + Top10SlowestBusAVL.toString()+"\n");
            System.out.println("Top 10 peores Búsquedas B(ms):" + Top10SlowestBusB.toString()+"\n");
            System.out.println("Top 10 peores Búsquedas BPlus(ms):" + Top10SlowestBusBPlus.toString()+"\n");
            System.out.println("Top 10 peores Búsquedas BAst(ms):" + Top10SlowestBusBAst.toString()+"\n\n");

            System.out.println("Top 10 peores Eliminaciones AVL(ms):" + Top10SlowestDelAVL.toString()+"\n");
            System.out.println("Top 10 peores Eliminaciones B(ms):" + Top10SlowestDelB.toString()+"\n");
            System.out.println("Top 10 peores Eliminaciones BPlus(ms):" + Top10SlowestDelBPlus.toString()+"\n");
            System.out.println("Top 10 peoresEliminaciones BAst(ms):" + Top10SlowestDelBAst.toString()+"\n");

        }

    }
}

