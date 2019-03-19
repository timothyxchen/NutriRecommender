//name: Tianxin Chen a-ID:tc2

package hw3;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Controller {

	//give recommendation of all the nutrients based on personal information
	class RecommendNutrientsButtonHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				Float ageText = 0f;
				Float weightText = 0f;
				Float heightText = 0f;
				Float physicalContent=0f;
				//if the input information is wrong, throw alert
				if(NutriByte.view.genderComboBox.getValue()==null) {
					NutriByte.view.initializePrompts(); 			
					throw new InvalidProfileException("Missing Gender information");
				}
				if(NutriByte.view.ageTextField.getText().isEmpty()) { 
					throw new InvalidProfileException("Missing Age information");
				}else {
					try{
						ageText = Float.valueOf(NutriByte.view.ageTextField.getText());
					}catch (NumberFormatException e) {
						throw new InvalidProfileException("Incorrect age input. Must be a number");
					}
					if(ageText<0) throw new InvalidProfileException("Age must be a positive number");
				}
				if(NutriByte.view.weightTextField.getText().isEmpty()) { 
					throw new InvalidProfileException("Missing weight information");
				}else {
					try{
						weightText = Float.valueOf(NutriByte.view.weightTextField.getText());
					}catch (NumberFormatException e) {
						throw new InvalidProfileException("Incorrect weight input. Must be a number");
					}
					if(weightText<0) throw new InvalidProfileException("Weight must be a positive number");
				}
				if(NutriByte.view.heightTextField.getText().isEmpty()) {
					throw new InvalidProfileException("Missing height information");
				}else {
					try{
						heightText = Float.valueOf(NutriByte.view.heightTextField.getText());
					}catch (NumberFormatException e) {
						throw new InvalidProfileException("Incorrect height input. Must be a number");
					}
					if(heightText<0) throw new InvalidProfileException("Height must be a positive number");
				}
				if(NutriByte.view.physicalActivityComboBox.getValue()==null) {
					throw new InvalidProfileException("Missing phyiscal activity information");
				}else {
					String physicalActivityText = NutriByte.view.physicalActivityComboBox.getValue();
					for(int i =0;i<NutriProfiler.PhysicalActivityEnum.values().length;i++){
						if(physicalActivityText.equals(NutriProfiler.PhysicalActivityEnum.values()[i].getName())) {
							physicalContent = NutriProfiler.PhysicalActivityEnum.values()[i].getPhysicalActivityLevel();//get the physical activity level by matching the names
						}
					}
				}
				String ingredientsToWatchText = NutriByte.view.ingredientsToWatchTextArea.getText();

				//create person and generate recommended nutrient tableview
				if(NutriByte.view.genderComboBox.getValue().equals("Male")){ 
					NutriByte.person = new Male(ageText, weightText, heightText, physicalContent, ingredientsToWatchText);
					NutriByte.person.initializeNutriConstantsTable();
					NutriByte.person.calculateEnergyRequirement();
					NutriByte.person.calculateNutriRequirement();
					NutriProfiler.createNutriProfile(NutriByte.person);
				}else { 
					NutriByte.person = new Female(ageText, weightText, heightText, physicalContent, ingredientsToWatchText);
					NutriByte.person.initializeNutriConstantsTable();
					NutriByte.person.calculateEnergyRequirement();
					NutriByte.person.calculateNutriRequirement();
					NutriProfiler.createNutriProfile(NutriByte.person);
				}
				NutriByte.view.recommendedNutrientsTableView.setItems(NutriByte.person.recommendedNutrientsList); 
			}catch(InvalidProfileException e) {
			}
		}			
	}

	//open the csv. or xml. file and record information into the labels
	class OpenMenuItemHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("static-access")
		@Override
		public void handle(ActionEvent event) {
			
			NutriByte.model.autoBindingIndex=1;			
			FileChooser fc = new FileChooser();
			fc.setTitle("Open File");
			fc.setInitialDirectory(new File(NutriByte.NUTRIBYTE_PROFILE_PATH));
			File file = fc.showOpenDialog(new Stage());

			if(file!=null&&NutriByte.model.readProfile(file.getAbsolutePath())==true) {
				NutriByte.view.ageTextField.setStyle("-fx-text-inner-color: black;");
				NutriByte.view.weightTextField.setStyle("-fx-text-inner-color: black;");
				NutriByte.view.heightTextField.setStyle("-fx-text-inner-color: black;");
				
				NutriByte.view.root.setCenter( NutriByte.view.nutriTrackerPane);
				NutriByte.view.initializePrompts();
				NutriByte.person.calculateEnergyRequirement();
				NutriByte.person.calculateNutriRequirement();
				NutriProfiler.createNutriProfile(NutriByte.person);
				NutriByte.view.recommendedNutrientsTableView.setItems(NutriByte.person.recommendedNutrientsList);

				if(NutriByte.person instanceof Male) {
					NutriByte.view.genderComboBox.getSelectionModel().select("Male");
				}else {
					NutriByte.view.genderComboBox.getSelectionModel().select("Female");
				}
				NutriByte.view.weightTextField.setText(String.format("%.2f",NutriByte.person.weight));
				NutriByte.view.ingredientsToWatchTextArea.setText(NutriByte.person.ingredientsToWatch);
				NutriByte.view.ageTextField.setText(String.format("%.2f",NutriByte.person.age));
				NutriByte.view.heightTextField.setText(String.format("%.2f",NutriByte.person.height));

				if(NutriByte.person.physicalActivityLevel==1f) {
					NutriByte.view.physicalActivityComboBox.getSelectionModel().select("Sedentary");
				}else if(NutriByte.person.physicalActivityLevel==1.1f) {
					NutriByte.view.physicalActivityComboBox.getSelectionModel().select("Low Active");
				}else if(NutriByte.person.physicalActivityLevel==1.25f) {
					NutriByte.view.physicalActivityComboBox.getSelectionModel().select("Active");
				}else if(NutriByte.person.physicalActivityLevel==1.48f) {
					NutriByte.view.physicalActivityComboBox.getSelectionModel().select("Very Active");
				}else {
					NutriByte.view.physicalActivityComboBox.getSelectionModel().select("Sedentary");
				}

				//if there are diet product information, pop the information
				if(NutriByte.person.dietProductsList.size()!=0) {
					NutriByte.view.productsComboBox.setItems(NutriByte.person.dietProductsList);
					NutriByte.view.productsComboBox.getSelectionModel().selectFirst();
					NutriByte.view.searchResultSizeLabel.setText(NutriByte.person.dietProductsList.size()+" product(s) found");
					for(String key:NutriByte.view.productsComboBox.getValue().productNutrients.keySet()) {
						Model.searchResultsList2.add(NutriByte.view.productsComboBox.getValue().productNutrients.get(key));
					} 
					NutriByte.view.productNutrientsTableView.setItems(Model.searchResultsList2);
					NutriByte.person.populateDietNutrientMap();
					NutriByte.view.nutriChart.updateChart();
					NutriByte.view.dietProductsTableView.setItems(NutriByte.person.dietProductsList);
				}

			}
		}
	}

	//generate new clear person and diet product file, clean the previous information
	class NewMenuItemHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("static-access")
		@Override
		public void handle(ActionEvent event) {	
			if(NutriByte.person !=null) {
				NutriByte.person.recommendedNutrientsList.clear();
				NutriByte.person.dietProductsList.clear();
				NutriByte.person.dietNutrientsMap.clear();
				NutriByte.model.searchResultsList.clear();
			}
			NutriByte.view.recommendedNutrientsTableView.getItems().clear();
			NutriByte.view.root.setCenter(NutriByte.view.nutriTrackerPane); 
			NutriByte.view.initializePrompts(); 		
			NutriByte.view.dietProductsTableView.getItems().clear();
			NutriByte.view.productNutrientsTableView.getItems().clear();
			NutriByte.view.productIngredientsTextArea.clear();
			NutriByte.view.servingSizeLabel.setText("");
			NutriByte.view.householdSizeLabel.setText("");
			NutriByte.view.productsComboBox.getItems().clear();
			NutriByte.view.productSearchTextField.setText("");
			NutriByte.view.nutrientSearchTextField.setText("");
			NutriByte.view.ingredientSearchTextField.setText("");
			NutriByte.view.searchResultSizeLabel.setText("");
			NutriByte.view.nutriChart.clearChart();
			NutriByte.model.autoBindingIndex=0;
			NutriByte.view.genderComboBox.setValue(null);
		}
	}

	//basic information about the program
	class AboutMenuItemHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("NutriByte");
			alert.setContentText("Version 3.0 \nRelease 1.0\nCopyleft Java Nerds\nThis software is designed purely for educational purposes.\nNo commercial use intended");
			Image image = new Image(getClass().getClassLoader().getResource(NutriByte.NUTRIBYTE_IMAGE_FILE).toString());
			ImageView imageView = new ImageView();
			imageView.setImage(image);
			imageView.setFitWidth(300);
			imageView.setPreserveRatio(true);
			imageView.setSmooth(true);
			alert.setGraphic(imageView);
			alert.showAndWait();
		}
	}

	//search for the product
	class SearchButtonHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			Model.searchResultsList2.clear();
			Model.searchResultsList.clear();
			NutriByte.view.productsComboBox.getItems().clear();
			String productSearch = NutriByte.view.productSearchTextField.getText();
			String nutrientSearch= NutriByte.view.nutrientSearchTextField.getText();
			String ingredientSearch = NutriByte.view.ingredientSearchTextField.getText();
			ObservableList<Product> productsComboBoxOption = FXCollections.observableArrayList();	
			for (String key : Model.productsMap.keySet()) {
				Model.searchResultsList.add(Model.productsMap.get(key));
			}

			//generate the searchResultsList
			if(!productSearch.isEmpty()) {
				for(int i =0;i<Model.searchResultsList.size();i++) {
					if(!Model.searchResultsList.get(i).getProductName().toLowerCase().contains(productSearch.toLowerCase())) {
						Model.searchResultsList.remove(Model.searchResultsList.get(i));
						i--;
					}
				}
			}
			if(!nutrientSearch.isEmpty()) {
				ObservableList<Product> temp = FXCollections.observableArrayList();
				for (int i=0;i<Model.searchResultsList.size();i++) {
					for(String key2:Model.searchResultsList.get(i).getProductNutrients().keySet()) {
						for(String key3:Model.nutrientsMap.keySet()) {
							if(Model.nutrientsMap.get(key3).getNutrientName().toLowerCase().contains(nutrientSearch.toLowerCase())) {
								if(Model.searchResultsList.get(i).getProductNutrients().get(key2).getNutrientCode().equals(Model.nutrientsMap.get(key3).getNutrientCode())) {
									temp.add(Model.searchResultsList.get(i));	
								}
							}
						}
					}
				}
				int status =0;
				for(int i =0;i<Model.searchResultsList.size();i++) {				
					for(int j=0;j<temp.size();j++) {					
						if(temp.get(j).getProductName().equals(Model.searchResultsList.get(i).getProductName())) {
							status=1;
						}
					}
					if(status==0) {
						Model.searchResultsList.remove(i);
						i--;
					}
					status=0;
				}
			}
			if(!ingredientSearch.isEmpty()) {
				for (int i=0;i<Model.searchResultsList.size();i++) {
					if(!Model.searchResultsList.get(i).getIngredients().toLowerCase().contains(ingredientSearch.toLowerCase())){
						Model.searchResultsList.remove(i);
						i--;
					}
				}

			}
			
			//put into productComboBox and enable selections
			for (int i=0;i<Model.searchResultsList.size();i++) {
				productsComboBoxOption.add(Model.searchResultsList.get(i));
			}
			NutriByte.view.productsComboBox.setItems(productsComboBoxOption);
			NutriByte.view.productsComboBox.getSelectionModel().selectFirst();
			NutriByte.view.searchResultSizeLabel.setText(String.valueOf(productsComboBoxOption.size())+" product(s) found");
		}
	}

	//close the current page, clear all previous information and return the welcome page
	class CloseButtonHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("static-access")
		@Override
		public void handle(ActionEvent event) {
			if(NutriByte.person !=null) {
				NutriByte.person.recommendedNutrientsList.clear();
				NutriByte.person.dietProductsList.clear();
				NutriByte.person.dietNutrientsMap.clear();
				NutriByte.model.searchResultsList.clear();
			}
			NutriByte.view.recommendedNutrientsTableView.getItems().clear();
			NutriByte.view.root.setCenter(NutriByte.view.nutriTrackerPane); 
			NutriByte.view.initializePrompts(); 		
			NutriByte.view.dietProductsTableView.getItems().clear();
			NutriByte.view.productNutrientsTableView.getItems().clear();
			NutriByte.view.productIngredientsTextArea.clear();
			NutriByte.view.servingSizeLabel.setText("");
			NutriByte.view.householdSizeLabel.setText("");
			NutriByte.view.productsComboBox.getItems().clear();
			NutriByte.view.productSearchTextField.setText("");
			NutriByte.view.nutrientSearchTextField.setText("");
			NutriByte.view.ingredientSearchTextField.setText("");
			NutriByte.view.searchResultSizeLabel.setText("");
			NutriByte.view.nutriChart.clearChart();
			NutriByte.view.root.setCenter( NutriByte.view.setupWelcomeScene());
		}

	}

	//clear the product search area and change product and diet tableview accordingly
	class ClearButtonHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			NutriByte.view.ingredientSearchTextField.clear();
			NutriByte.view.productNutrientsTableView.getItems().clear();
			NutriByte.view.nutrientSearchTextField.clear();
			NutriByte.view.productSearchTextField.clear();
			NutriByte.view.productIngredientsTextArea.clear();
			NutriByte.view.searchResultSizeLabel.setText("");	
			NutriByte.view.servingSizeLabel.setText("0.00");
			NutriByte.view.householdSizeLabel.setText("0.00");
			NutriByte.view.dietServingUomLabel.setText("");
			NutriByte.view.dietHouseholdUomLabel.setText("");
			NutriByte.view.productsComboBox.getItems().clear();
		}	
	}


	//add the diet into the dietProductsLists and pop the diet tableview
	class addDietButtonHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("static-access")
		@Override
		public void handle(ActionEvent event) {
			NutriByte.view.nutriChart.clearChart();
			if(NutriByte.view.productsComboBox.getSelectionModel().getSelectedIndex()>=0) {
				Product p = NutriByte.view.productsComboBox.getSelectionModel().getSelectedItem();
				Product p2= new Product(p.getNdbNumber(),p.getProductName(),p.getManufacturer(),p.getIngredients());
				p2.setHouseholdUom(p.getHouseholdUom());
				p2.setServingUom(p.getServingUom());
				p2.setHouseholdSize(p.getHouseholdSize());
				p2.setServingSize(p.getServingSize());
				p2.setProductNutrients(p.getProductNutrients());
				if(!NutriByte.view.dietServingSizeTextField.getText().isEmpty()) {
					if(p2.getServingSize()!=0f) {
						p2.setServingSize(Float.valueOf(NutriByte.view.dietServingSizeTextField.getText()));
						p2.setHouseholdSize(Float.valueOf(NutriByte.view.dietServingSizeTextField.getText())/p.getServingSize()*p.getHouseholdSize());	
					}
				}else if(!NutriByte.view.dietHouseholdSizeTextField.getText().isEmpty()) {
					if(p2.getHouseholdSize()!=0f) {
						p2.setHouseholdSize(Float.valueOf(NutriByte.view.dietHouseholdSizeTextField.getText()));
						p2.setServingSize(Float.valueOf(NutriByte.view.dietHouseholdSizeTextField.getText())/p.getHouseholdSize()*p.getServingSize());
					}
				}else if(!NutriByte.view.dietServingSizeTextField.getText().isEmpty()&&!NutriByte.view.dietHouseholdSizeTextField.getText().isEmpty()) {
					p2.setServingSize(Float.valueOf(NutriByte.view.dietServingSizeTextField.getText()));	
					p2.setHouseholdSize(Float.valueOf(NutriByte.view.dietServingSizeTextField.getText())/p.getServingSize()*p.getHouseholdSize());
				}
				NutriByte.person.dietProductsList.add(p2);
				NutriByte.view.dietProductsTableView.setItems(NutriByte.person.dietProductsList);
			}
			//if there is no person info, then pop the list and create chart accordingly
			if(NutriByte.person!=null) {
				NutriByte.person.populateDietNutrientMap();
				NutriByte.view.nutriChart.updateChart();
			}			
		}	
	}

	//remove the products from the dietProductsList
	class RemoveDietButtonHandler implements EventHandler<ActionEvent> {
		@SuppressWarnings("static-access")
		@Override
		public void handle(ActionEvent event) {
			int i = NutriByte.view.dietProductsTableView.getSelectionModel().getSelectedIndex();
			if (i>=0) {
				if(NutriByte.person!=null) {
					NutriByte.person.dietProductsList.remove(i);
					NutriByte.person.populateDietNutrientMap();
					NutriByte.view.nutriChart.updateChart();   
					NutriByte.view.dietProductsTableView.setItems(NutriByte.person.dietProductsList);
				}else {
					NutriByte.person.dietProductsList.remove(i);
					NutriByte.view.dietProductsTableView.setItems(NutriByte.person.dietProductsList);
				}
			}
		}
	}

	//save the file and throw alert message when there is incorrect information
	class SaveMenuMenuItemHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			try {
				int exceptionCount=0;
				if (NutriByte.view.genderComboBox.getValue() == null) {
					exceptionCount++;
					throw new InvalidProfileException("Missing gender information");
				}
				if (NutriByte.view.ageTextField.getText().isEmpty()) {
					exceptionCount++;
					throw new InvalidProfileException("Missing age information");
				}
				try {
					float age = Float.parseFloat(NutriByte.view.ageTextField.getText().trim());
					if(age < 0) {
						exceptionCount++;
						throw new InvalidProfileException("Age must be a postive number");
					}else if(age>150) {
						exceptionCount++;
						throw new InvalidProfileException("Max age is 150"); 
					}
				} catch (NumberFormatException e) {
					exceptionCount++;
					new InvalidProfileException("Incorrect age input. Must be a number");
					return;
				}
				if (NutriByte.view.weightTextField.getText().isEmpty()) {
					exceptionCount++;
					throw new InvalidProfileException("Missing weight information");
				}
				try {
					float weight = Float.parseFloat(NutriByte.view.weightTextField.getText());
					if(weight < 0) {
						exceptionCount++;
						throw new InvalidProfileException("Weight must be a postive number");
					}
				} catch (NumberFormatException e) {
					exceptionCount++;
					new InvalidProfileException("Incorrect weight input.Must be a number");
					return;
				}

				if (NutriByte.view.heightTextField.getText().isEmpty()) {
					exceptionCount++;
					throw new InvalidProfileException("Missing height information");
				}
				try {
					float height = Float.parseFloat(NutriByte.view.heightTextField.getText());
					if(height < 0) {
						exceptionCount++;
						throw new InvalidProfileException("Height must be a postive number");
					}
				} catch (NumberFormatException e) {
					exceptionCount++;
					new InvalidProfileException("Incorrect height input.Must be a number");
					return;
				}
				
				if(exceptionCount==0) {
					FileChooser fc = new FileChooser();
					fc.getExtensionFilters().addAll(new ExtensionFilter("Excel", "*.csv"));
					fc.setInitialDirectory(new File(NutriByte.NUTRIBYTE_PROFILE_PATH));
					File file = null;
					file = fc.showSaveDialog(new Stage());
					if (file != null) NutriByte.model.writeProfile(file.getPath());
				}		
			}catch(InvalidProfileException e) {
			}		
		}
	}

}
