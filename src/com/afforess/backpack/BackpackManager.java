package com.afforess.backpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.utils.ChatUtils;

public class BackpackManager {
	public static ConcurrentHashMap<String, Object> config = new ConcurrentHashMap<String, Object>();
	
	public static void initialize() {
		//initialize data folders
		File dir = new File(Backpack.directory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		dir = new File(Backpack.dataDirectory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		try {
			readConfig();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void readConfig() throws ParserConfigurationException, TransformerException, SAXException, IOException {
		File configuration = new File(Backpack.directory + File.separator + "config.xml");
		
		//Build XML file
		if (!configuration.exists()) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			//root elements
			Document doc = docBuilder.newDocument();
			doc.setXmlStandalone(true);
			Element rootElement = doc.createElement("Configuration");
			doc.appendChild(rootElement);
			
			//players elements
			Element playerList = doc.createElement("Players");
			rootElement.appendChild(playerList);
			
			//players elements
			Element player = doc.createElement("Player");
			playerList.appendChild(player);
			
			//name elements
			Element name = doc.createElement("Name");
			name.appendChild(doc.createTextNode("default"));
			player.appendChild(name);
			
			Comment comment = doc.createComment("Users not explicitly added to this list use the default settings");
			player.insertBefore(comment,name);
			
			//inventory pages allowed elements
			Element pages = doc.createElement("InventoryPages");
			pages.appendChild(doc.createTextNode("9"));
			player.appendChild(pages);
			
			player = doc.createElement("Player");
			playerList.appendChild(player);
			name = doc.createElement("Name");
			name.appendChild(doc.createTextNode("Afforess"));
			player.appendChild(name);
			pages = doc.createElement("InventoryPages");
			pages.appendChild(doc.createTextNode("9"));
			player.appendChild(pages);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(configuration);
			transformer.transform(source, result);
		}
		
		else {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(configuration);
		    doc.getDocumentElement().normalize();
		    
		    NodeList nList = doc.getElementsByTagName("Player");
		    for (int temp = 0; temp < nList.getLength(); temp++) {
		 
		       Node nNode = nList.item(temp);	    
		       if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
		          Element eElement = (Element) nNode;
		 
		          String name = getTagValue("Name",eElement);
		          String pages = getTagValue("InventoryPages",eElement);
		          int inventoryPages = 1;
		          try {
		        	  inventoryPages = Integer.parseInt(pages);
		          }
		          catch (Exception e) {
		        	  Backpack.log.severe("[Backpack] Failed to Read the Configuration for Player: " + name + "!");
		          }
		          config.put(name, inventoryPages);
		        }
		    }
		    
		    if (config.get("default") == null) {
		    	Backpack.log.warning("[Backpack] No default configuration set! Using 9 as default!");
		    }
		}
	}
	
	private static String getTagValue(String sTag, Element eElement){
	    NodeList nlList= eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	    Node nValue = (Node) nlList.item(0); 
	 
	    return nValue.getNodeValue();    
	 }

	public static void initializeBackPack(BackpackPlayer player) {
		//Check for previous data
		File prevData = new File(player.getDataFilePath());
		if (prevData.exists()){
			Scanner input;
			try {
				input = new Scanner(prevData);
				int page = 0;
				ItemStack[] contents = new ItemStack[player.getContents().length];
				int item = 0;
				try {
					String first = input.nextLine();
					int currentPage = Integer.parseInt(first.split(":")[1]);
					player.setCurrentInventoryPage(currentPage);
				}
				catch (Exception e) {
					player.setCurrentInventoryPage(0);
				}
				while(input.hasNext()) {
					String line = input.nextLine();
					//Move to next page
					if (line.contains("page:")) {
						page = Integer.parseInt(line.split(":")[1]);
						player.setInventoryPage(page, contents);
						contents = new ItemStack[player.getContents().length];
						item = 0;
					}
					//Parse this pages contents
					else {
						ItemStack i = deserializeItemStackString(line);
						//System.out.println("page:" + page + " item:" + i);
						contents[item] = i;
						item++;
					}
				}
				input.close();
				player.setInventoryPage(player.getCurrentInventoryPage(), player.getInventoryPage(player.getCurrentInventoryPage()));
			} catch (FileNotFoundException e) {
			}
		}
		else {
			if (player.getMaxInventoryPages() > 1)
				ChatUtils.sendMultilineMessage(player.getPlayer(), "This server is using the Backpack mod! [NEWLINE] Use the mouse wheel while sneaking to switch through your [NEWLINE] new " + player.getMaxInventoryPages() + " inventory pages!", ChatColor.GREEN.toString());

		}
		for (int i = 0; i < 9; i++) {
			if (player.getInventoryPage(i) == null)
				player.setInventoryPage(i, new ItemStack[player.getContents().length]);
		}
		player.setBackpackEnabled(true);
		
	}
	
	public static String serializeItemStack(ItemStack i) {
		if (i== null) {
			return "null";
		}
		StringBuilder s = new StringBuilder();
		s.append(i.getTypeId());
		s.append(":");
		s.append(i.getAmount());
		s.append(":");
		s.append(i.getDurability());
		return s.toString();
	}
	
	public static ItemStack deserializeItemStackString(String s) {
		if (s.equals("null")) {
			return null;
		}
		String[] split = s.split(":");
		ItemStack item = new ItemStack(Integer.parseInt(split[0]), Integer.parseInt(split[1]), (short) Integer.parseInt(split[2]));
		return item;
	}
	
	public static boolean isEqual(ItemStack item1, ItemStack item2) {
		if (item1 == null && item2 != null) return false;
		if (item1 != null && item2 == null) return false;
		if (item1 == null && item2 == null) return true;
		if (item1.getTypeId() != item2.getTypeId()) return false;
		if (item1.getAmount() != item2.getAmount()) return false;
		if (item1.getDurability() != item2.getDurability()) return false;
		
		return true;
	}
}
