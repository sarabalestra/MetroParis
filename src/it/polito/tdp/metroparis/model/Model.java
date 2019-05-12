package it.polito.tdp.metroparis.model;


import java.util.List;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {

	//1) GRAFO: semplice, orientato, non pesato
	
	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate;
	
	
	
	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}



	public void creaGrafo() {
		
		//Creo l'oggetto grafo vuoto di tipo Fermata:
		this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);
	
		//Riempio il grafo:
		/*1. AGGIUNGO I VERTICI:
		 *i vertici sono le fermate che sono salvate nel db, 
		 *perr ricavarle uso il metodo del dao getAllFermate().
		--> salvo le fermate in una lista e le passo al metodo per l'aggiunta dei vertici*/
		
		MetroDAO dao= new MetroDAO();
		this.fermate = dao.getAllFermate();
		Graphs.addAllVertices(this.grafo, fermate);
		
		/*2.AGGIUNGO GLI ARCHI, 
		 *gli archi sono le connessioni.
		 *NB: due vertici saranno collegati da un arco solo se esiste almeno una Connessione tra le due fermate. */
	
		/*MODO1: prendo tutte le possibili coppie di Fermate, tra queste due Fermate c'è una Connessione?
		 *       SI: aggiungo l'arco NO: non faccio niente*/
		//NUMERO ALTO DI QUERY (numVertici^2) ---> METODO MOLTO LENTO!!!
		for(Fermata partenza : this.grafo.vertexSet()) {
			for(Fermata arrivo : this.grafo.vertexSet()) {
				
				//esisteConnessione(p,a) interroga il db
				if(dao.esisteConnessione(partenza, arrivo)) {
					this.grafo.addEdge(partenza, arrivo);
				}
				
			}
		}
		
		
		/*MODO2: per ogni fermata di partenza chiedo al db che mi fornisca l'elenco
		 * 		 delle fermate di arrivo.
		 * 	Dopo posso rapidamente aggiungere tutti gli archi al grafo.*/
		for(Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza);
			
			for(Fermata arrivo : arrivi) {
			/*Questo arrivo è una stazione di comodo (finta), che è EQUALS (con l'id-->VEDI DAO)
			 * con una stazione già presente nel grafo.*/	
				this.grafo.addEdge(partenza, arrivo);
			}
		}
		
		/*MODO3: la query mi restituisce direttamente le connessioni.
		 * */
		
		
	}

}
