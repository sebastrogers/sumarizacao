package sumarizacao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.omg.PortableInterceptor.ForwardRequestHelper;

import Jama.Matrix;



public class Sumarizacao {

    final int MAX_SENTENCES = 4;
    private Map<Integer, String> content;
    private List<String> sentences;
    private String title;
    private String line;
    private int blank = 0;
    private String local;
    private Integer numeroDoc = 0;
    private List<String> palavrasExtraidas;
    private Map<String, List<Integer>> docs;
    private Map<String, Map<Integer, Integer>> palavraFreqDoc;
    private List<String> doc;
    private Matrix matriz;
    private Map<Integer, Integer> palavrasExtraidasDoc;

    String diretorio = "";

    public Sumarizacao() {
    	palavraFreqDoc = new HashMap<>();
        palavrasExtraidasDoc = new HashMap<>();
        docs = new HashMap<>();
        doc = new ArrayList<>();
        palavrasExtraidas = new ArrayList<>();

    }

    public void loadFile(String dir) {

        File dirBaseP = new File(dir);

        for (String list : dirBaseP.list()) {
        	palavraFreqDoc = new HashMap<>();
            palavrasExtraidasDoc = new HashMap<>();
            docs = new HashMap<>();
            doc = new ArrayList<>();
            palavrasExtraidas = new ArrayList<>();
            numeroDoc = 0;

            BufferedReader lerArq = null;
            try {
                String f = dir + "/" + list;
                local = f;
                System.out.println(f);
                lerArq = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
                String linha = lerArq.readLine();
                int aux = 0;
                blank = 0;
                content = new HashMap<>();
                while (linha != null) {

                    
                    if (linha.equals("") || linha.equals(" ")) {
                        linha = lerArq.readLine();
                        
                        continue;
                    }
                    content.put(aux, linha);
                    aux++;

                    if (!linha.equals("") && !linha.equals(" ")) {
                        
                        linha = eliminarCaractere(linha);
                        

                        String[] palavras = linha.split(" ");

                                           
                        
                        
                        int count = 0;
                        for (String p : palavras) {

                            p = p.toLowerCase();

                            if (this.docs.containsKey(p)) {
                                List l = docs.get(p);
                                l.add(numeroDoc);
                                this.docs.put(p, l);
                                if (count == 0) {
                                    count++;
                                }
                                
                            } else if (StopWord.getStopwords().containsKey(p)) {

                            } else {

                                List<Integer> l = new ArrayList<>();
                                l.add(numeroDoc);
                                this.docs.put(p, l);
                                palavrasExtraidas.add(p);
                                count++;
                                


                            }
                            if (palavraFreqDoc.containsKey(p)) {
                                Map<Integer, Integer> m = palavraFreqDoc.get(p);
                                m.put(numeroDoc, count);
                                palavraFreqDoc.put(p, m);
                            } else {
                                Map<Integer, Integer> m = new HashMap<>();
                                m.put(numeroDoc, count);
                                palavraFreqDoc.put(p, m);
                            }
                            
                            

                        }

                   
                        palavrasExtraidasDoc.put(numeroDoc, count);
                        numeroDoc++;
                        doc.add(linha);
                    }

                    linha = lerArq.readLine();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Sumarizacao.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Sumarizacao.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    lerArq.close();
                } catch (IOException ex) {
                    Logger.getLogger(Sumarizacao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
      
            matrizFrequencia();
            matrizTFIDF();
            SimilaridadeTFIDFAndCentralidade();

        }
    }
    
    public void matrizFrequencia() {

        matriz = new Matrix(docs.size(), numeroDoc);
        int aux = 0;

        for (String c : palavrasExtraidas) {

            List<Integer> value = docs.get(c);

            for (Integer vl : value) {

                double v = matriz.get(aux, vl) + 1.0;
                matriz.set(aux, vl, v);
            }
            aux++;
        }

        
    }

    public void matrizTFIDF() {

        int i = 0;
        
        for (String c : palavrasExtraidas) {

            for (int j = 0; j < numeroDoc; j++) {


                
                matriz.set(i, j, TF_IDF.calculoTFIDF(matriz.get(i, j), (double) palavrasExtraidasDoc.get(j), (double) numeroDoc, (double) palavraFreqDoc.get(c).size()));
            }
            i++;
        }
        System.out.println();
        System.out.println();
        System.out.println();
        matriz.print(0, 3);
    }

    public String eliminarCaractere(String sentenca){
    	for (String caractere : StopWord.getCaracteres()) {
        	
            
            sentenca = sentenca.replaceAll("[" + caractere + "]", "");

        
    }
    	return sentenca;
    }

    
    public void SimilaridadeTFIDFAndCentralidade() {
    	
    	SimilaridadeTFIDF sTFIDF = new SimilaridadeTFIDF(matriz, docs.size(), numeroDoc);
    	Centralidade sCentralidade = new Centralidade(matriz, docs.size(), numeroDoc);
    	Map<String, Double> mTFIDF = sTFIDF.calcularSimilaridadeTFIDF();
    	Map<String, Double> cTFIDF = sTFIDF.calcularSimilaridadeTFIDF();
    	/*
    	List ls = new LinkedList();
    	ls.addAll(mTFIDF.values());
    	Collections.sort(ls);
    	
    	List lc = new LinkedList();
    	lc.addAll(cTFIDF.values());
    	Collections.sort(lc);
        */
    	Map<String, Double> media = new HashMap<>();
    	for(int i = 0; i < numeroDoc; i++){
    		double d = mTFIDF.get(""+i)+cTFIDF.get(""+i);
    		media.put(doc.get(i), d/2);
    	}
    	
    	
    	List ls = new LinkedList();
    	ls.addAll(media.values());
    	Collections.sort(ls);
    	
    	media = sortByValue(media);
    	
    	List<String> stack = new ArrayList<>(); 
    	
    	for(Map.Entry<String, Double> entrySet:media.entrySet()){
    		String k = entrySet.getKey();
    		Double v = entrySet.getValue();
    		System.out.println(k);
    		System.out.println(v);
    		stack.add(k);
    	}
    	
    	FileWriter arq = null;
    	try {
			arq = new FileWriter(local + ".txt", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	PrintWriter gravarArq = new PrintWriter(arq);    	
    	for(int i = media.size()-1; i > 0; i--){
    		System.out.println(stack.get(i));
    		
        	gravarArq.printf("%s", stack.get(i) + ". ");
    	}
    	
    	try {
			arq.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) { 
    	Map<K,V> result = new LinkedHashMap<>(); 
    	Stream <Entry<K,V>> st = map.entrySet().stream(); 
    	st.sorted(Comparator.comparing(e -> e.getValue())) .forEachOrdered(e ->result.put(e.getKey(),e.getValue())); 
    	return result; 
    	}

    public static void main(String[] args) {

        String dir = "C:/Users/sebas/ppgi/sumarizacao/resumos";

        Sumarizacao s = new Sumarizacao();

        s.loadFile(dir);

        
    }
}