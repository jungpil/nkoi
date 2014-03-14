package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import landscape.InfluenceMatrix;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simulation.Case;
import simulation.Simulator.SimulatorType;
import agent.Innovator;
import agent.Provider;

public class ConfigReader {

	/**
	 * Parse the input config xml file. Convert each case node into a case
	 * object and return a list of those case objects
	 * 
	 * @param xmlFileName
	 *            a string, which directs to the input config xml file
	 * @return a list of case objects
	 */
	public ArrayList<Case> read(String xmlFileName) {
		ArrayList<Case> result = new ArrayList<Case>();
		try {
			File fXmlFile = new File(xmlFileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			// reference -
			// http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			NodeList caseList = doc.getElementsByTagName("case");

			for (int i = 0; i < caseList.getLength(); i++) {
				result.add(this.constructCase(caseList.item(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Convert a case node to a case object
	 * 
	 * @param caseNode
	 *            a node object, which represents a case node in the config xml
	 *            file
	 * @return a case object
	 */
	private Case constructCase(Node caseNode) {
		int runs = -1;
		InfluenceMatrix inf = null;
		HashSet<SimulatorType> typeList = new HashSet<SimulatorType>();
		ArrayList<Innovator> innovatorList = new ArrayList<Innovator>();
		ArrayList<Provider> providerList = new ArrayList<Provider>();

		NodeList settings = caseNode.getChildNodes();
		for (int i = 0; i < settings.getLength(); i++) {
			Node settingNode = settings.item(i);
			if (settingNode.getNodeType() == Node.ELEMENT_NODE) {
				Element setting = (Element) settingNode;
				if (setting.getTagName().equals("runs")) {
					runs = new Integer(setting.getTextContent().trim());
				} else if (setting.getTagName().equals("inf")) {
					inf = this.constructInf(setting.getTextContent().trim());
				} else if (setting.getTagName().equals("strategy")) {
					typeList.add(this.constructType(setting.getTextContent()
							.trim()));
				} else if (setting.getTagName().equals("innovator")) {
					innovatorList.addAll(this.constructInnovators(setting));
				} else if (setting.getTagName().equals("provider")) {
					providerList.addAll(this.constructProviders(setting));
				} else {
					System.out.println("WARNING : unknown case element "
							+ setting.getTagName());
				}
			}
		}
		for (int i = 0; i < innovatorList.size(); i++) {
			if (inf.getN() < innovatorList.get(i).getMSize()
					+ innovatorList.get(i).getPSize()) {
				System.out
						.println("ERROR : Innovator's M and P are larger than N");
				System.exit(1);
			}
			innovatorList.get(i).setId(i);
		}
		for (int i = 0; i < providerList.size(); i++) {
			if (inf.getN() < providerList.get(i).getQSize()) {
				System.out.println("ERROR : Provider's Q is larger than N");
				System.exit(1);
			}
			providerList.get(i).setId(i);
		}
		return new Case(runs, inf, typeList, innovatorList, providerList);
	}

	/**
	 * Return an influence matrix object according to the given file
	 * 
	 * @param infFileName
	 *            a string, which directs to the file of the influence matrix
	 * @return an influence matrix object according to the file indicated by the
	 *         given file name
	 */
	private InfluenceMatrix constructInf(String infFileName) {
		// create inf matrix
		int matrix[][] = null;
		// start reading
		try {
			FileReader fRead = new FileReader(infFileName);
			BufferedReader bufRead = new BufferedReader(fRead);
			String line = bufRead.readLine();
			int lineCount = 0;
			while (line != null) {
				String tokens[] = line.split(",");
				if (matrix == null) { // first line
					matrix = new int[tokens.length][tokens.length];
				}
				for (int i = 0; i < tokens.length; i++) {
					matrix[lineCount][i] = (tokens[i].equals("x") ? 1 : 0);
				}
				line = bufRead.readLine();
				lineCount++;
			}
			fRead.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new InfluenceMatrix(matrix);
	}

	/**
	 * Convert a string to a simulator type
	 * 
	 * @param strategy
	 *            a string, that represents a simulator type
	 * @return a simulator type
	 */
	private SimulatorType constructType(String strategy) {
		if (strategy.equalsIgnoreCase("CLOSED")) {
			return SimulatorType.CLOSED;
		} else if (strategy.equalsIgnoreCase("LICENSING")) {
			return SimulatorType.LICENSING;
		} else if (strategy.equalsIgnoreCase("OUTSOURCING")) {
			return SimulatorType.OUTSOURCING;
		} else if (strategy.equalsIgnoreCase("ALLIANCE_MAX")) {
			return SimulatorType.ALLIANCE_MAX;
		} else if (strategy.equalsIgnoreCase("ALLIANCE_MIN")) {
			return SimulatorType.ALLIANCE_MIN;
		} else {
			System.out.println("ERROR : unknown strategy type " + strategy);
			System.exit(1);
			return null;
		}
	}

	/**
	 * Covert an innovator node to a list of innovator objects.
	 * 
	 * @param agentNode
	 *            an innovator node
	 * @return a list of innovator objects
	 */
	private ArrayList<Innovator> constructInnovators(Node agentNode) {
		int num = -1;
		int power = -1;
		int M = -1;
		int P = -1;
		NodeList attrList = agentNode.getChildNodes();
		for (int i = 0; i < attrList.getLength(); i++) {
			Node attrNode = attrList.item(i);
			if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
				Element attr = (Element) attrNode;
				if (attr.getTagName().equals("num")) {
					num = new Integer(attr.getTextContent().trim());
				} else if (attr.getTagName().equals("power")) {
					power = new Integer(attr.getTextContent().trim());
				} else if (attr.getTagName().equals("M")) {
					M = new Integer(attr.getTextContent().trim());
				} else if (attr.getTagName().equals("P")) {
					P = new Integer(attr.getTextContent().trim());
				} else {
					System.out.println("WARNING : unknown innovator attribute "
							+ attr.getTagName());
				}
			}
		}
		ArrayList<Innovator> result = new ArrayList<Innovator>();
		for (int i = 0; i < num; i++) {
			result.add(new Innovator(power, M, P));
		}
		return result;
	}

	/**
	 * Covert an provider node to a list of provider objects.
	 * 
	 * @param agentNode
	 *            an provider node
	 * @return a list of provider objects
	 */
	private ArrayList<Provider> constructProviders(Node agentNode) {
		int num = -1;
		int power = -1;
		int Q = -1;
		NodeList attrList = agentNode.getChildNodes();
		for (int i = 0; i < attrList.getLength(); i++) {
			Node attrNode = attrList.item(i);
			if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
				Element attr = (Element) attrNode;
				if (attr.getTagName().equals("num")) {
					num = new Integer(attr.getTextContent().trim());
				} else if (attr.getTagName().equals("power")) {
					power = new Integer(attr.getTextContent().trim());
				} else if (attr.getTagName().equals("Q")) {
					Q = new Integer(attr.getTextContent().trim());
				} else {
					System.out.println("WARNING : unknown provider attribute "
							+ attr.getTagName());
				}
			}
		}
		ArrayList<Provider> result = new ArrayList<Provider>();
		for (int i = 0; i < num; i++) {
			result.add(new Provider(power, Q));
		}
		return result;
	}

}
