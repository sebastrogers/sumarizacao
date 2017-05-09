package sumarizacao;

import java.util.HashMap;
import java.util.Map;

import Jama.Matrix;

public class SimilaridadeTFIDF {
	private Matrix m;
	private int linha = 0;
	private int coluna = 0;
	public SimilaridadeTFIDF(Matrix m, int linha, int coluna){
		this.m = m;
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public Map <String, Double> calcularSimilaridadeTFIDF(){
		Map<String, Double> r = new HashMap<>();
		for(int i = 0; i < coluna; i++){
			double d = 0.0;
			for(int j = 0; j < linha; j++){
				d += m.get(j, i);
				
				
			}
			r.put(""+i, d);
		}
		return r;
	}
}
