//name: Tianxin Chen a-ID:tc2

package hw3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class NutriByte extends Application{
	static Model model = new Model();  	//made static to make accessible in the controller
	static View view = new View();		//made static to make accessible in the controller
	static Person person;				//made static to make accessible in the controller

	Controller controller = new Controller();	//all event handlers 

	/**Uncomment the following three lines if you want to try out the full-size data files */
//		static final String PRODUCT_FILE = "data/Products.csv";
//		static final String NUTRIENT_FILE = "data/Nutrients.csv";
//		static final String SERVING_SIZE_FILE = "data/ServingSize.csv";

	/**The following constants refer to the data files to be used for this application */
	static final String PRODUCT_FILE = "data/Nutri2Products.csv";
	static final String NUTRIENT_FILE = "data/Nutri2Nutrients.csv";
	static final String SERVING_SIZE_FILE = "data/Nutri2ServingSize.csv";

	static final String NUTRIBYTE_IMAGE_FILE = "NutriByteLogo.png"; //Refers to the file holding NutriByte logo image 

	static final String NUTRIBYTE_PROFILE_PATH = "profiles";  //folder that has profile data files

	static final int NUTRIBYTE_SCREEN_WIDTH = 1015;
	static final int NUTRIBYTE_SCREEN_HEIGHT = 675;

	@Override
	public void start(Stage stage) throws Exception {
		model.readProducts(PRODUCT_FILE);
		model.readNutrients(NUTRIENT_FILE);
		model.readServingSizes(SERVING_SIZE_FILE );
		view.setupMenus();
		view.setupNutriTrackerGrid();
		view.root.setCenter(view.setupWelcomeScene());
		Background b = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
		view.root.setBackground(b);
		Scene scene = new Scene (view.root, NUTRIBYTE_SCREEN_WIDTH, NUTRIBYTE_SCREEN_HEIGHT);
		view.root.requestFocus();  //this keeps focus on entire window and allows the textfield-prompt to be visible
		setupBindings();
		stage.setTitle("NutriByte 3.0");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	//set up actions for all the buttons, create callback for columns and set up listeners
	void setupBindings() { 
		view.newNutriProfileMenuItem.setOnAction(controller.new NewMenuItemHandler());
		view.openNutriProfileMenuItem.setOnAction(controller.new OpenMenuItemHandler());
		view.searchButton.setOnAction(controller.new SearchButtonHandler());
		view.clearButton.setOnAction(controller.new ClearButtonHandler());
		view.addDietButton.setOnAction(controller.new addDietButtonHandler());
		view.removeDietButton.setOnAction(controller.new RemoveDietButtonHandler());
		view.saveNutriProfileMenuItem.setOnAction(controller.new SaveMenuMenuItemHandler());
		view.closeNutriProfileMenuItem.setOnAction(controller.new CloseButtonHandler());
		view.exitNutriProfileMenuItem.setOnAction(event -> Platform.exit());
		view.aboutMenuItem.setOnAction(controller.new AboutMenuItemHandler());
		view.createProfileButton.setOnAction(controller.new RecommendNutrientsButtonHandler());

		view.recommendedNutrientNameColumn.setCellValueFactory(recommendedNutrientNameCallback);
		view.recommendedNutrientQuantityColumn.setCellValueFactory(recommendedNutrientQuantityCallback);
		view.recommendedNutrientUomColumn.setCellValueFactory(recommendedNutrientUomCallback);
		view.productNutrientNameColumn.setCellValueFactory(productNutrientNameCallback);
		view.productNutrientQuantityColumn.setCellValueFactory(productNutrientQuantityCallback);
		view.productNutrientUomColumn.setCellValueFactory(productNutrientUomCallback);
 		view.dietServingSizeColumn.setCellValueFactory(dietProductServingsizeCallBack);		

		recommendedTableBinding.addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				person = newValue;
				NutriProfiler.createNutriProfile(person);
				view.recommendedNutrientsTableView.setItems(person.recommendedNutrientsList);
			}
		});
		
		//change searchResultsList and productNutrientsTableView when productComboBox is changed, and generate information of the product
		NutriByte.view.productsComboBox.valueProperty().addListener((observable,oldValue,newValue)->{
			if(NutriByte.view.productsComboBox.getSelectionModel().getSelectedIndex()>=0) {
				Model.searchResultsList2.clear();
				for(String key:NutriByte.view.productsComboBox.getValue().productNutrients.keySet()) {
					Model.searchResultsList2.add(NutriByte.view.productsComboBox.getValue().productNutrients.get(key));
				}
				NutriByte.view.productNutrientsTableView.setItems(Model.searchResultsList2);
				NutriByte.view.productIngredientsTextArea.setText(NutriByte.view.productsComboBox.getValue().getIngredients());
				NutriByte.view.servingSizeLabel.setText(String.format("%.2f",NutriByte.view.productsComboBox.getValue().getServingSize())+" "+NutriByte.view.productsComboBox.getValue().getServingUom());
				NutriByte.view.householdSizeLabel.setText(String.format("%.2f",NutriByte.view.productsComboBox.getValue().getHouseholdSize())+" "+NutriByte.view.productsComboBox.getValue().getHouseholdUom());
				NutriByte.view.dietServingUomLabel.setText(NutriByte.view.productsComboBox.getValue().getServingUom());
				NutriByte.view.dietHouseholdUomLabel.setText(NutriByte.view.productsComboBox.getValue().getHouseholdUom());
			}
		});

	}

	//Bind recommended nutrients table with the information of the person
	ObjectBinding<Person> recommendedTableBinding = new ObjectBinding<Person>() {
		{
			super.bind(view.genderComboBox.valueProperty(), view.ageTextField.textProperty(),
					view.weightTextField.textProperty(), view.heightTextField.textProperty(),
					view.physicalActivityComboBox.valueProperty(), view.ingredientSearchTextField.textProperty());
		}

		//create the person based on inputs
		protected Person computeValue() {
			if (view.genderComboBox.getValue()!= null&&model.autoBindingIndex==0) {
				String genderText = view.genderComboBox.getValue();
				String ingredientsToWatchText = view.ingredientsToWatchTextArea.getText();
				String physicalActivityText = null;

				if (view.physicalActivityComboBox.getValue() == null) {
					physicalActivityText = NutriProfiler.PhysicalActivityEnum.SEDENTARY.getName();
				} else {
					physicalActivityText = view.physicalActivityComboBox.getValue();
				}
				Float physicalActivity=0f;
				for(int i =0;i<NutriProfiler.PhysicalActivityEnum.values().length;i++){
					if(physicalActivityText.equals(NutriProfiler.PhysicalActivityEnum.values()[i].getName())) {
						physicalActivity = NutriProfiler.PhysicalActivityEnum.values()[i].getPhysicalActivityLevel();//get the physical activity level by matching the names
					}
				}
				float age = 0;
				float weight = 0;
				float height = 0;
				TextField textField = null;

                try {
                    textField = view.ageTextField;
                    textField.setStyle("-fx-text-inner-color: black;");
                    age = Float.parseFloat(textField.getText().trim());
                    if (age < 0)
                        throw new NumberFormatException();

                    textField = view.weightTextField;
                    textField.setStyle("-fx-text-inner-color: black;");
                    weight = Float.parseFloat(textField.getText().trim());
                    if (weight < 0)
                        throw new NumberFormatException();

                    textField = view.heightTextField;
                    textField.setStyle("-fx-text-inner-color: black;");
                    height = Float.parseFloat(textField.getText().trim());
                    if (height < 0)
                        throw new NumberFormatException();

                    Person result = null;
                    if (genderText.equals("Female")) {
                        result = new Female(age, weight, height, physicalActivity, ingredientsToWatchText);
                    } else if (genderText.equals("Male")) {
                        result = new Male(age, weight, height, physicalActivity, ingredientsToWatchText);
                    }
                    return result;
                } catch (NumberFormatException e) {
                    textField.setStyle("-fx-text-inner-color: red;");
                    return null;
                }
			}
			return null;
		}
	};
	
	//callback for recommended nutrient columns
	Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>> recommendedNutrientNameCallback = new Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<RecommendedNutrient, String> arg0) {
			Nutrient nutrient = Model.nutrientsMap.get(arg0.getValue().getNutrientCode());
			return nutrient.nutrientNameProperty();
		}
	};	

	Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>> recommendedNutrientQuantityCallback = new Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<RecommendedNutrient, String> arg0) {
			for(RecommendedNutrient rn:person.recommendedNutrientsList) {
				if(rn.getNutrientCode().equals(arg0.getValue().getNutrientCode())) {
					return (new SimpleStringProperty(String.format("%.2f",rn.getNutrientQuantity()))); //return float value with only two decimal points
				}
			}
			return null;
		}
	};

	Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>> recommendedNutrientUomCallback = new Callback<CellDataFeatures<RecommendedNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<RecommendedNutrient, String> arg0) {
			Nutrient nutrient = Model.nutrientsMap.get(arg0.getValue().getNutrientCode());
			return nutrient.nutrientUomProperty();		
		}
	};

	//callback for product nutrient columns
	Callback<CellDataFeatures<Product.ProductNutrient, String>,ObservableValue<String>> productNutrientNameCallback = new Callback<CellDataFeatures<Product.ProductNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<Product.ProductNutrient, String> arg0) {
			Nutrient nutrient = Model.nutrientsMap.get(arg0.getValue().getNutrientCode());
			return nutrient.nutrientNameProperty();
		}
	};

	Callback<CellDataFeatures<Product.ProductNutrient, String>,ObservableValue<String>> productNutrientQuantityCallback = new Callback<CellDataFeatures<Product.ProductNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<Product.ProductNutrient, String> arg0) {
			for(Product.ProductNutrient rn:Model.searchResultsList2) {
				if(rn.getNutrientCode().equals(arg0.getValue().getNutrientCode())) {
					return (new SimpleStringProperty(String.format("%.2f",rn.getNutrientQuantity()))); //return float value with only two decimal points
				}
			}
			return null;
		}
	};

	Callback<CellDataFeatures<Product.ProductNutrient, String>,ObservableValue<String>> productNutrientUomCallback = new Callback<CellDataFeatures<Product.ProductNutrient, String>, ObservableValue<String>>() {
		@Override
		public ObservableValue<String> call(CellDataFeatures<Product.ProductNutrient, String> arg0) {
			Nutrient nutrient = Model.nutrientsMap.get(arg0.getValue().getNutrientCode());
			return nutrient.nutrientUomProperty();	
		}
	};

	//callback for diet product information columns
	Callback<CellDataFeatures<Product, Float>, ObservableValue<Float>>  dietProductServingsizeCallBack
	= new Callback<CellDataFeatures<Product, Float>,ObservableValue<Float>>() {
		@SuppressWarnings("static-access")
		@Override
		public ObservableValue<Float> call(CellDataFeatures<Product, Float> param) {
			for(Product p:NutriByte.person.dietProductsList) {
				if(p.getProductName().equals(param.getValue().getProductName())&&p.getHouseholdSize().equals(param.getValue().getHouseholdSize())&&p.getServingSize().equals(param.getValue().getServingSize())) {
					return (new SimpleFloatProperty(Float.valueOf(p.getServingSize()))).asObject();				
				}				
			}
			return null;
		}
	};

	Callback<CellDataFeatures<Product, String>, ObservableValue<String>>  dietProductServingUomCallBack
	= new Callback<CellDataFeatures<Product, String>,ObservableValue<String>>() {
		@SuppressWarnings("static-access")
		@Override
		public ObservableValue<String> call(CellDataFeatures<Product, String> param) {
			for(Product p:NutriByte.person.dietProductsList) {
				if(p.getNdbNumber().equals(param.getValue().getNdbNumber())) {
					return (new SimpleStringProperty(p.getServingUom()));
				}				
			}
			return null;
		}
	};
}
