import java.awt.Graphics;
import java.util.*;

import javax.xml.bind.DataBindingException;

// Change A
// Change E

/** Adjacent Node */
class AdjNode {
	AdjNode(int i, int w) {
		index = i;
		weight = w;
	}

	int index;
	int weight;
	AdjNode next = null;
	boolean visited = false;
}

/** Vertex Node */
class VerNode {
	VerNode() {
	}

	VerNode(String s) {
		str = new String(s);
	}

	String str;
	AdjNode next;

	@Override
	public String toString() {
		return str;
	}
}

class Dijkstra {
	public Dijkstra(int n) {
		D = new int[n];
		P = new int[n];
		S = new boolean[n];
	}
	// D[i] is the length of the shortest path from the
	// Source Point("from") to Point i.
	int[] D;
	// P[i] is the index of the last point of the shortest
	// path from the Source Point to Point i.
	int[] P;
	// S[i] means if the Point i has been generated.
	boolean[] S;
}

public class Graph {
	public int getIndex(String s) {
		for (int i = 0; i < n; i++) {
			if (list.get(i).toString().equals(s)) {
				return i;
			}
		}
		return -1;
	}

	public String getText(int index) {
		return list.get(index).toString();
	}

	public List<Integer> getNeighbors(int index) {
		List<Integer> l = new ArrayList<Integer>();
		AdjNode an = list.get(index).next;
		if (an != null) {
			do {
				l.add(an.index);
				an = an.next;
			} while (an != null);
		}
		return l;
	}
	
	public List<AdjNode> getUnvisitedNeighbors(int index) {
		List<AdjNode> l = new ArrayList<AdjNode>();
		AdjNode an = list.get(index).next;
		if (an != null) {
			do {
				if (!an.visited)
					l.add(an);
				an = an.next;
			} while (an != null);
		}
		return l;
	}

	public int getWeight(int from, int to) {
		if (from == to) return 0;
		AdjNode an = list.get(from).next;
		if (an != null) {
			do {
				if (an.index == to) {
					return an.weight;
				}
				an = an.next;
			} while (an != null);
		}
		return INFINITY;
	}

	public int addVertex(String s) {
		list.add(new VerNode(s));
		n++;
		return n - 1;
	}

	public void addEdge(String from, String to) {
		// Adding the first word.
		if (from == null) {
			addVertex(to);
			return;
		}

		int a = getIndex(from);
		if (a == -1)
			a = addVertex(from);
		int b = getIndex(to);
		if (b == -1)
			b = addVertex(to);

		AdjNode p = new AdjNode(b, 1);
		if (list.get(a).next == null) {
			list.get(a).next = p;
			e++;
		} else {
			boolean found = false;
			AdjNode t = list.get(a).next;
			AdjNode q = t;
			while (t != null) {
				if (t.index == b) {
					t.weight++;
					found = true;
					break;
				}
				q = t;
				t = t.next;
			}
			if (!found) {
				q.next = p;
				e++;
			}
		}
	}
	
	public void cleanVisitedFlags() {
		for (int i = 0; i < n; i++) {
			AdjNode an = list.get(i).next;
			if (an != null) {
				do {
					an.visited = false;
					an = an.next;
				} while (an != null);
			}
		}
	}

	private final int INFINITY = 100000000;
	
	private int minCost(int v, Dijkstra d) {
		int min = INFINITY;
		int index = 0;
		for (int i = 0; i < n; i++) {
			if (!d.S[i] && d.D[i] < min) {
				min = d.D[i];
				index = i;
			}
		}
		return index;
	}

	public List<String> getBridgeWords(String word1, String word2) {
		List<String> l = new ArrayList<String>();
		int a = getIndex(word1), b = getIndex(word2);
		if (a == -1 || b == -1) {
			return null;
		}
		List<Integer> neighbors = getNeighbors(a);
		for (int c : neighbors) {
			int w = getWeight(c, b);
			if (w < INFINITY && w > 0) {
				l.add(getText(c));
			}
		}
		return l;
	}

	public Dijkstra calcShortestPath(int from) {
		Dijkstra d = new Dijkstra(n);
		for (int i = 0; i < n; i++) {
			d.D[i] = getWeight(from, i);
			d.S[i] = false;
			d.P[i] = from;
		}
		d.S[from] = true;
		d.P[from] = -1;

		for (int i = 0; i < n; i++) {
			if (i != from) {
				int w = minCost(from, d);
				d.S[w] = true;
				for (int j = 0; j < n; j++) {
					if (!d.S[j]) {
						int sum = d.D[w] + getWeight(w, j);
						if (sum < d.D[j]) {
							d.D[j] = sum;
							d.P[j] = w;
						}
					}
				}
			}
		}
		return d;
	}

	public String getShortestPath(Dijkstra d, int from, int to) {
		StringBuilder sb = new StringBuilder();
		Stack<Integer> s = new Stack<Integer>();
		s.push(to);
		int t = to;
		while (d.P[t] != -1 && d.P[t] != from) {
			s.push(d.P[t]);
			t = d.P[t];
		}
		sb.append(getText(from));
		while (!s.isEmpty()) {
			sb.append("->");
			sb.append(getText(s.pop()));
		}
		sb.append(". length:");
		if (d.D[to] == INFINITY) sb.append("INF");
		else sb.append(d.D[to]);
		return sb.toString();
	}

	public int getNumberOfVertex() {
		return n;
	}

	public int getNumberOfEdge() {
		return e;
	}

	private List<VerNode> list = new ArrayList<VerNode>();
	private int n = 0, e = 0;

	/** 绘制带箭头的直线，r为箭头长度 */
	private void drawLineWithArrow(Graphics g, int x1, int y1, int x2, int y2, int r) {
		if (x1 == x2) return;
		g.drawLine(x1, y1, x2, y2);
		double theta = Math.atan((y1 - y2) / (double) (x2 - x1));
		if (x2 < x1)
			theta += Math.PI;
		double alpha = theta + Math.PI / 12.0;
		double beta = theta - Math.PI / 12.0;
		g.drawLine((int) (x2 - r * Math.cos(alpha)), (int) (y2 + r * Math.sin(alpha)), x2, y2);
		g.drawLine((int) (x2 - r * Math.cos(beta)), (int) (y2 + r * Math.sin(beta)), x2, y2);
	}

	/** 绘制有向图，x,y为中心位置，r为全图半径 */
	public void drawGraph(Graphics g, int x, int y, int r) {
		int[] px = new int[n];
		int[] py = new int[n];
		for (int i = 0; i < n; i++) {
			double theta = 2 * Math.PI * i / n;
			px[i] = x + (int) (r * Math.sin(theta));
			py[i] = y - (int) (r * Math.cos(theta));
			g.drawString(Integer.toString(i), px[i] + (int) (5 * Math.sin(theta)), py[i] - (int) (5 * Math.cos(theta)));
			g.drawRoundRect(px[i] + (int) (5 * Math.sin(theta)) - 3, py[i] - (int) (5 * Math.cos(theta)) - 12, 25, 15,
					2, 2);

		}
		for (int i = 0; i < n; i++) {
			VerNode v = list.get(i);
			if (v.next != null) {
				AdjNode p = v.next;
				do {
					drawLineWithArrow(g, px[i], py[i], px[p.index], py[p.index], 15);
					g.drawString(Integer.toString(p.weight), (int) ((3 * px[i] + px[p.index]) / 4.0),
							(int) ((3 * py[i] + py[p.index]) / 4.0));
					p = p.next;
				} while (p != null);
			}
		}
	}

	/** 绘制图例 */
	public void drawText(Graphics g, int x, int y) {
		for (int i = 0; i < n; i++) {
			g.drawString(Integer.toString(i) + "  " + list.get(i).toString(), x, y + i * 10);
		}
	}

}
