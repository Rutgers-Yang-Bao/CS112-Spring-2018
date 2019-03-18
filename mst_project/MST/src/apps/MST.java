package apps;

import structures.*;
import java.util.ArrayList;
import java.util.List;

import apps.PartialTree.Arc;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
		PartialTreeList tree = new PartialTreeList();
		for (int i=0; i < graph.vertices.length; i++) {
			PartialTree temp = new PartialTree(graph.vertices[i]);
			for (Vertex.Neighbor nbr=graph.vertices[i].neighbors; nbr != null; nbr=nbr.next) {
				Vertex v1 = graph.vertices[i];
				Vertex v2 = nbr.vertex;
				int weight = nbr.weight;
				Arc a = new Arc(v1,v2,weight);
				MinHeap<Arc> b = temp.getArcs();
				b.insert(a);
			}
			tree.append(temp);
		}
		
		return tree;
		
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(PartialTreeList ptlist) {
		ArrayList<PartialTree.Arc> answer = new ArrayList<>();
		
		while(ptlist.size() > 1) {
			PartialTree T1 = ptlist.remove();
			Arc temp = T1.getArcs().getMin();
			while(getParent(temp.v1).equals(getParent(temp.v2))) {
				T1.getArcs().deleteMin();
				temp = T1.getArcs().getMin();
			}
			T1.getArcs().deleteMin();
			PartialTree T2 = ptlist.removeTreeContaining(temp.v2);
			T1.merge(T2);
			ptlist.append(T1);
			answer.add(temp);
		}
		

		return answer;
	}
	
	private static String getParent(Vertex v) {
		String s = v.parent.name;
		while(!s.equals(v.name)) {
			v = v.parent;
			s = v.parent.name;
		}
		return s;
	}
}
