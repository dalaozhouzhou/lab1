import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

// Change A

// Change D

// Change C


class MyPanel extends JPanel {

	private Graph graph;

	public MyPanel(Graph gr) {
		graph = gr;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		graph.drawGraph(g, 500, 470, 450);
		graph.drawText(g, 1050, 20);
	}
}

public class TextGraph extends JFrame {

	/** 展示有向图 */
	public void showDirectedGraph(Graph G, String filename) {
		MyPanel p = new MyPanel(graph);
		int width = 1200, height = 1000;
		p.setSize(width, height);
		this.add(p);
		this.setSize(width, height);
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = image.createGraphics();
		p.paint(g2);
		try {
			ImageIO.write(image, "jpeg", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 查询桥接词 */
	public String queryBridgeWords(String word1, String word2) {
		List<String> l = graph.getBridgeWords(word1, word2);
		if (l == null) {
			return "No " + word1 + " or " + word2 + " in the graph!";
		}
		StringBuilder sb = new StringBuilder();
		for (String word : l) {
			sb.append(word);
			sb.append(' ');
		}
		if (sb.length() == 0) {
			return "No bridge words from " + word1 + " to " + word2 + "!";
		} else {
			return "The bridge words from " + word1 + " to " + word2 +
				   " are: " + sb.toString() + ".";
		}
	}

	/** 根据 Bridge Word 生成新文本 */
	public String generateNewText(String inputText) {
		String s;
		StringBuilder sb = new StringBuilder();
		s = inputText.replaceAll("[\\p{Punct}]+", " ");
		s = s.replaceAll("\\s+", " ");
		String[] words = s.split(" ");
		for (int i = 0; i < words.length; i++) {
			sb.append(words[i]);
			sb.append(' ');
			if (i + 1 < words.length) {
				List<String> l = graph.getBridgeWords(words[i], words[i + 1]);
				if (l != null && l.size() > 0) {
					Random r = new Random();
					sb.append(l.get(r.nextInt(l.size())));
					sb.append(' ');
				}
			}
		}
		return sb.toString().trim();
	}

	/** 计算最短路径  */
	public String calcShortestPath(String word1, String word2) {
		int from = graph.getIndex(word1), to = graph.getIndex(word2);
		Dijkstra d = graph.calcShortestPath(from);
		return graph.getShortestPath(d, from, to);
	}
	
	/** 计算多条最短路径  */
	public String calcShortestPaths(String word1) {
		int from = graph.getIndex(word1);
		Dijkstra d = graph.calcShortestPath(from);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < graph.getNumberOfVertex(); i++) {
			if (i != from) {
				sb.append(graph.getShortestPath(d, from, i));
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	/** 随机游走  */
	public String randomWalk() {
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		Scanner s = new Scanner(System.in);
		int i = r.nextInt(graph.getNumberOfVertex());
		String str = graph.getText(i);
		sb.append(str);
		sb.append(' ');
		System.out.print(str + " ");
		while (true) {
			String si = s.nextLine();
			if (si.equals("x")) break;
			
			List<AdjNode> l = graph.getUnvisitedNeighbors(i);
			if (l.size() == 0) break;
			int j = r.nextInt(l.size());
			AdjNode an = l.get(j);
			an.visited = true;
			str = graph.getText(an.index);
			sb.append(str);
			sb.append(' ');
			System.out.print(str + " ");
			i = an.index;
		}
		//s.close();
		return sb.toString();
	}

	private Graph graph = new Graph();

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("输入文件路径：");
		String str = s.nextLine();
		// String str = "D:\\temp\\1.txt";
		// s.close();
		File f = new File(str);
		Reader reader = null;
		TextGraph tg = new TextGraph();
		try {
			reader = new InputStreamReader(new FileInputStream(f));
			int t;
			StringBuilder sb = new StringBuilder();
			String a = null, b = null;
			while (true) {
				t = reader.read();
				char c = (char) t;
				if (c >= 'A' && c <= 'Z') {
					c += ('a' - 'A');
				}
				if (c >= 'a' && c <= 'z') {
					sb.append(c);
				} else {
					if (sb.length() > 0) {
						a = b;
						b = sb.toString();
						tg.graph.addEdge(a, b);
						sb.delete(0, sb.length());
					}
					if (t == -1)
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			System.out.print("0. 退出\n" +
					"1. 展示有向图\n" +
					"2. 查询桥接词\n" +
					"3. 根据桥接词生成新文本\n" +
					"4. 计算最短路径\n" +
					"5. 随机游走\n" +
					"选择功能：");
			String opt = s.next();
			s.nextLine(); // Drop the \n
			if (opt.equals("0")) break;
			switch (opt) {
			case "1":
				System.out.println("输入存储路径：");
				String sav = s.nextLine();
				tg.showDirectedGraph(tg.graph, sav);
				break;
			case "2":
				System.out.println("输入两个单词，以空格分割：");
				String word = s.nextLine();
				String[] wordlist = word.split(" ");
				if (wordlist.length == 2) {
					System.out.println(tg.queryBridgeWords(wordlist[0], wordlist[1]));
				} else {
					System.out.println("输入错误");
				}
				break;
			case "3":
				System.out.println("输入文本：");
				String text = s.nextLine();
				System.out.println(tg.generateNewText(text));
				break;
			case "4":
				System.out.println("输入1或2个单词，空格分割：");
				String word2 = s.nextLine();
				String[] wordlist2 = word2.split(" ");
				if (wordlist2.length == 1) {
					System.out.println(tg.calcShortestPaths(wordlist2[0]));
				} else if (wordlist2.length == 2) {
					System.out.println(tg.calcShortestPath(wordlist2[0], wordlist2[1]));
				} else {
					System.out.println("输入错误");
				}
				break;
			case "5":
				System.out.println("输入保存路径：");
				String savt = s.nextLine();
				System.out.println("游走开始，回车继续，输入x停止");
				tg.graph.cleanVisitedFlags();
				String wtext = tg.randomWalk();
				File f1 = new File(savt);
				FileWriter fw = null;
				BufferedWriter bw = null;
				try {
					if (!f1.exists()) f1.createNewFile();
					fw = new FileWriter(f1.getAbsoluteFile());
					bw = new BufferedWriter(fw);
					bw.write(wtext);
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
		s.close();
	}
}
