//name: Tianxin Chen a-ID:tc2

package hw3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CSVFiler extends DataFiler {
	String gender = "";
	float age = 0f;
	float weight = 0f;
	float height = 0f;
	float physicalActivityLevel = 1.0f;
	StringBuilder ingredientsToWatch = new StringBuilder();

	@SuppressWarnings("static-access")
	@Override
	public boolean readFile(String filename) {
		//reset when reading the file
		NutriByte.person.dietProductsList.clear();
		NutriByte.view.productIngredientsTextArea.clear();
		NutriByte.view.dietProductsTableView.getItems().clear();
		NutriByte.view.productNutrientsTableView.getItems().clear();
		NutriByte.view.nutriChart.clearChart();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
		} catch (Exception e) {
		}
		StringBuilder sb = new StringBuilder();
		while (scanner.hasNextLine()) {
			sb.append(scanner.nextLine() + "\n");
		}

		String[] data = sb.toString().split("\n");
		if(validatePersonData(data[0])==null) {
			return false;
		}
		for (int i = 1; i < data.length; i++) {
			validateProductData(data[i]);
		}
		return true;
	}

	@SuppressWarnings("resource")
	@Override
	public void writeFile(String filename) {
		try {
			//write in the information into the bufferwriter
			BufferedWriter bw = new BufferedWriter(new FileWriter(filename));			
			gender = NutriByte.view.genderComboBox.getValue();
			bw.append(gender+",");	
			bw.append(NutriByte.view.ageTextField.getText().trim()+",");
			bw.append(NutriByte.view.weightTextField.getText()+",");			
			bw.append(NutriByte.view.heightTextField.getText()+",");
			
			String physicalActivityText = NutriByte.view.physicalActivityComboBox.getValue();
			if (physicalActivityText.toLowerCase().equals("Sedentary".toLowerCase()))
				bw.append("1");
			if (physicalActivityText.toLowerCase().equals("Low Active".toLowerCase()))
				bw.append("1.1");
			if (physicalActivityText.toLowerCase().equals("Active".toLowerCase()))
				bw.append("1.25");
			if (physicalActivityText.toLowerCase().equals("Very Active".toLowerCase()))
				bw.append("1.48");
			bw.append(",");
			if(NutriByte.view.ingredientsToWatchTextArea.getText().isEmpty()) {
				bw.append(" ");
				bw.append("\n");
			}else {
				bw.append(NutriByte.view.ingredientsToWatchTextArea.getText());
				bw.append("\n");
			}
			for (Product product : Person.dietProductsList) {
				bw.append(product.getNdbNumber());
				bw.append(",");
				bw.append(product.getServingSize().toString());
				bw.append(",");
				bw.append(product.getHouseholdSize().toString());
				bw.append("\n");
			}
			bw.close();
		} catch (InvalidProfileException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

	//return person if information is correct, else return null
	private Person validatePersonData(String data) {
		String[] lineArray = data.split(",", -1);
		int exceptionCount =0;
		try {
			try {
				if (lineArray.length < 6) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("Missing data");
				}else if (!lineArray[0].equals("Female") && !lineArray[0].equals("Male")) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("The profile must have gender:Female or Male as first word");
				}else if (isNumber(lineArray[1]) == false) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("Invalid data for Age: " + lineArray[1]+"\nAge must be a number");
				}else if (isNumber(lineArray[2]) == false) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("Invalid data for Weight: " + lineArray[2]+"\nWeight must be a number");
				}else if (isNumber(lineArray[3]) == false) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("Invalid data for Height: " + lineArray[3] + "\nHeight must be a number");
				}else if (isNumber(lineArray[4]) == false) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("Invalid data for physical activity level: " +lineArray[4]+"\nMust be: 1.0, 1.1, 1.25 or 1.48");
				}else if (!isPhysicalActivity(lineArray[4])) {
					NutriByte.view.recommendedNutrientsTableView.getItems().clear();
					NutriByte.view.initializePrompts();
					throw new InvalidProfileException("Invalid physical activity level: "+lineArray[4]+"\nMust be: 1.0, 1.1, 1.25 or 1.48");
				}
			}catch (InvalidProfileException e) {
				exceptionCount++;
				throw new InvalidProfileException("could not read the file");
			}
			
			//create person if all information is correct
			if(exceptionCount==0) {
				gender=lineArray[0].trim();
				age=Float.parseFloat(lineArray[1].trim());
				weight=Float.parseFloat(lineArray[2].trim());
				height=Float.parseFloat(lineArray[3].trim());
				physicalActivityLevel=Float.parseFloat(lineArray[4].trim());

				for(int i = 5;i<lineArray.length-1;i++)
				{
					ingredientsToWatch.append(lineArray[i].trim()+", ");
				}
				ingredientsToWatch.append(lineArray[lineArray.length-1]);

				switch(gender)
				{
				case "Female":
					NutriByte.person = new Female(age, weight, height, physicalActivityLevel, ingredientsToWatch.toString());
					break;
				case "Male":
					NutriByte.person = new Male(age, weight, height, physicalActivityLevel, ingredientsToWatch.toString());
					break;
				default:
					break;
				}
				return NutriByte.person;
			}else {
				return null;
			}
		}catch(InvalidProfileException e) {		
		}	
		return null;
	}

	//check if the information of the product is correct
	@SuppressWarnings("static-access")
	public Product validateProductData(String data) {
		int exceptionCount =0;
		String[] lineArray = data.trim().split(",");
		try {
			if (lineArray.length < 3) {
				throw new InvalidProfileException("Cannot read: "+data
						+"\nThe data must be - String, number, number - for ndb number, serving size, household size");
			}else if (!Model.productsMap.containsKey(lineArray[0].toString().trim())) {
				throw new InvalidProfileException("No product found with this code: "+lineArray[0].toString().trim());
			}else if (isNumber(lineArray[1].toString().trim()) == false || isNumber(lineArray[2].toString().trim()) == false) {
				throw new InvalidProfileException("Cannot read: "+data
						+"\nThe data must be - String, number, number - for ndb number, serving size, household size");
			}
		} catch (InvalidProfileException e) {
			exceptionCount++;
		}

		//return the product if information is correct, else return null
		if(exceptionCount==0) {
			Product product = Model.productsMap.get(lineArray[0].trim());
			product.setServingSize(Float.parseFloat(lineArray[1].trim()));
			product.setHouseholdSize(Float.parseFloat(lineArray[2].trim()));
			NutriByte.person.dietProductsList.add(product);
			return product;
		}else {
			return null;
		}
	}

	//test if the input is a number
	private static boolean isNumber( String input ){
	   try{
	      Float.parseFloat(input);
	      return true;
	   }catch( Exception e )
	   {
	      return false;
	   }
	}

	//test if the physical activity input is valid
	private static boolean isPhysicalActivity(String input) {
		if (input!=null && !input.equals("")) {
			if(Float.parseFloat(input.trim()) == 1f || Float.parseFloat(input.trim())==1.25f||Float.parseFloat(input.trim()) == 1.1f|| Float.parseFloat(input.trim()) == 1.48f)
				return true;
		}
		return false;
	}

}