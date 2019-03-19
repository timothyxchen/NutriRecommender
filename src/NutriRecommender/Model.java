//name: Tianxin Chen a-ID:tc2

package hw3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class Model { 
	static ObservableMap<String, Product> productsMap = FXCollections.observableHashMap();
	static ObservableMap<String, Nutrient> nutrientsMap = FXCollections.observableHashMap();
	static ObservableList<Product> searchResultsList = FXCollections.observableArrayList();
	static ObservableList<Product.ProductNutrient> searchResultsList2 = FXCollections.observableArrayList();
	int autoBindingIndex=0;

	
	//this method read the product file into the productsMap with ndbNumber as the key and Product object 
	public void readProducts(String filename) {
		Scanner s=null;
		try {
			s = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder prodSb = new StringBuilder();
		s.nextLine();
		while(s.hasNextLine()) {
			prodSb.append(s.nextLine()+"\n");
		}
		String[] temp = prodSb.toString().split("\n");

		for(int i = 0;i<temp.length;i++) {
			temp[i] = "\","+temp[i] +",\"";
		}
		for(int i =0;i<temp.length;i++) {
			if(temp[i].split("\",\"",-1)[8].isEmpty()) {
				Product product= new Product(temp[i].split("\",\"")[1],temp[i].split("\",\"")[2],temp[i].split("\",\"")[5],temp[i].split("\",\"",-1)[8]);
				productsMap.put(temp[i].split("\",\"")[1], product);
			}else {
				Product product= new Product(temp[i].split("\",\"")[1],temp[i].split("\",\"")[2],temp[i].split("\",\"")[5],temp[i].split("\",\"")[8]);
				productsMap.put(temp[i].split("\",\"")[1], product);
			}
		}
	}

	//this method read the nutrient file into the nutrientsMap with nutrientCode as the key and Nutrient object 
	public void readNutrients(String filename) {
		Scanner s=null;
		try {
			s = new Scanner(new FileReader(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder prodSb = new StringBuilder();
		s.nextLine();
		while(s.hasNextLine()) {
			prodSb.append(s.nextLine()+"\n");
		}
		String[] temp = prodSb.toString().split("\n");

		for(int i = 0;i<temp.length;i++) {
			temp[i] = "\","+temp[i] +",\"";
		}
		for(int i =0;i<temp.length;i++) {
			Nutrient nutrient= new Nutrient(temp[i].split("\",\"")[2],temp[i].split("\",\"")[3],temp[i].split("\",\"")[6]);
			nutrientsMap.put(temp[i].split("\",\"")[2], nutrient); //store nutrient info and replace the duplicate ones
			Product p = productsMap.get(temp[i].split("\",\"",-1)[1]); 
			if(Float.valueOf(temp[i].split("\",\"",-1)[5])!=0) {
				Product.ProductNutrient pd =  p.new ProductNutrient(temp[i].split("\",\"")[2],Float.valueOf(temp[i].split("\",\"")[5]));//read product nutrient info and store in the map
				p.productNutrients.put(temp[i].split("\",\"",-1)[2], pd); 
			}
		}
	}

	//this method read the servingSize file into the productsMap to store products servingSize info  
	public void readServingSizes(String filename){
		Scanner s=null;
		try {
			s = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder serveSb = new StringBuilder();
		s.nextLine();
		while(s.hasNextLine()) {
			serveSb.append(s.nextLine()+"\n");
		}
		String[] temp = serveSb.toString().split("\n");
		for(int i = 0;i<temp.length;i++) {
			temp[i] = "\","+temp[i] +",\"";
		}
		for(int i =0;i<temp.length;i++) {
			String ndbNumber=temp[i].split("\",\"")[1];
			if(productsMap.containsKey(ndbNumber)){
				Product p = productsMap.get(ndbNumber);
				if(!temp[i].split("\",\"",-1)[2].isEmpty()) {
					p.setServingSize(Float.valueOf(temp[i].split("\",\"",-1)[2]));
				}if(!temp[i].split("\",\"",-1)[3].isEmpty()) {
					p.setServingUom(temp[i].split("\",\"",-1)[3]);
				}if(!temp[i].split("\",\"",-1)[4].isEmpty()) {
					p.setHouseholdSize(Float.valueOf(temp[i].split("\",\"",-1)[4]));
				}if(!temp[i].split("\",\"",-1)[5].isEmpty()) {
					p.setHouseholdUom(temp[i].split("\",\"",-1)[5]);
				}
			}
		}
	}	

	//this method checks the file name to call different methods in DataFiler
	public boolean readProfile(String filename) {
		autoBindingIndex=1;
		DataFiler df = null;
		if (filename.substring(filename.length() - 3).equals("csv")) {
			df = new CSVFiler();
			return df.readFile(filename);
		} else if (filename.substring(filename.length() - 3).equals("xml")) {
			df = new XMLFiler();
			return df.readFile(filename);
		}else {
			return false;
		}
	}
	
	public void writeProfile(String filename) {
		CSVFiler cv=new CSVFiler();
		cv.writeFile(filename);
	}
}
