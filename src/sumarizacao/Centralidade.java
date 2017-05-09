package sumarizacao;

import java.util.HashMap;
import java.util.Map;

import Jama.Matrix;

public class Centralidade {
	private Matrix m;
	private int linha = 0;
	private int coluna = 0;
	private Map<Integer, Double> v;
	public Centralidade(Matrix m, int linha, int coluna){
		this.m = m;
		this.linha = linha;
		this.coluna = coluna;
		this.v = new HashMap<>();
	}
	
	public Map <String, Double> calcularCentralidade(){
		Map<String, Double> r = new HashMap<>();
		for(int i = 0; i < linha; i++){
			double d = 0.0;
			
			for(int j = 0; j < coluna; j++){
				d += m.get(i, j);
				
				
			}
			v.put(i, d);
		}
		for(int i = 0; i < linha; i++){
			double d = 0.0;
			
			for(int j = 0; j < coluna; j++){
				d += m.get(j, i)/v.get(i);
				
			}
			r.put(""+i, d);
		}
		return r;
	}

}
