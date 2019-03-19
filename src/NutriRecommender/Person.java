//name: Tianxin Chen a-ID:tc2

package hw3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public abstract class Person {

	float age, weight, height, physicalActivityLevel; //age in years, weight in kg, height in cm
	String ingredientsToWatch;
	ObservableList<RecommendedNutrient> recommendedNutrientsList = FXCollections.observableArrayList(); 
	static ObservableList<Product> dietProductsList = FXCollections.observableArrayList();
	ObservableMap<String, RecommendedNutrient> dietNutrientsMap = FXCollections.observableHashMap(); 
	

	float[][] nutriConstantsTable = new float[NutriProfiler.RECOMMENDED_NUTRI_COUNT][NutriProfiler.AGE_GROUP_COUNT];

	NutriProfiler.AgeGroupEnum ageGroup;

	abstract void initializeNutriConstantsTable();
	abstract float calculateEnergyRequirement();

	//	//remove this default constructor once you have defined the child's constructor
	//	Person() {}

	Person(float age, float weight, float height, float physicalActivityLevel, String ingredientsToWatch) {
		this.age=age;
		this.weight=weight;
		this.height=height;
		this.physicalActivityLevel=physicalActivityLevel;
		this.ingredientsToWatch=ingredientsToWatch;	
		for(int i =0;i<NutriProfiler.AgeGroupEnum.values().length;i++){
			if(age<NutriProfiler.AgeGroupEnum.values()[i+1].getAge()) {
				this.ageGroup=NutriProfiler.AgeGroupEnum.values()[i+1];
				break;
			}
		}
	}

	//returns an array of nutrient values of size NutriProfiler.RECOMMENDED_NUTRI_COUNT. 
	//Each value is calculated as follows:
	//For Protein, it multiples the constant with the person's weight.
	//For Carb and Fiber, it simply takes the constant from the 
	//nutriConstantsTable based on NutriEnums' nutriIndex and the person's ageGroup
	//For others, it multiples the constant with the person's weight and divides by 1000.
	//Try not to use any literals or hard-coded values for age group, nutrient name, array-index, etc. 

	float[] calculateNutriRequirement() {
		
		float[] nutriRequirement = new float[NutriProfiler.RECOMMENDED_NUTRI_COUNT];
		nutriRequirement[0]= weight*nutriConstantsTable[0][ageGroup.getAgeGroupIndex()];
		nutriRequirement[1]= nutriConstantsTable[1][ageGroup.getAgeGroupIndex()];
		nutriRequirement[2]= nutriConstantsTable[2][ageGroup.getAgeGroupIndex()];
		for(int i =3;i<NutriProfiler.RECOMMENDED_NUTRI_COUNT;i++) {
			nutriRequirement[i]= nutriConstantsTable[i][ageGroup.getAgeGroupIndex()]*weight/1000;
		}
		return nutriRequirement;
	}
	
	//populate the dietNutrientMap based on the dietProductsList
	void populateDietNutrientMap() {
		dietNutrientsMap.clear();
		for(String key:Model.nutrientsMap.keySet()) {
			RecommendedNutrient rn=new RecommendedNutrient();
			rn.setNutrientCode(key);
			rn.setNutrientQuantity(0f);
			dietNutrientsMap.put(key, rn);
		}
		for(int i=0;i<dietProductsList.size();i++) {
			for(String key:dietProductsList.get(i).productNutrients.keySet() ) {
				for(String key2:dietNutrientsMap.keySet()) {
					if(key.equals(key2)) {
						Float nutriSum = dietNutrientsMap.get(key2).getNutrientQuantity()
								+dietProductsList.get(i).productNutrients.get(key).getNutrientQuantity()*dietProductsList.get(i).getServingSize()/100;
						RecommendedNutrient rn = new RecommendedNutrient();		
						rn.setNutrientCode(key);
						rn.setNutrientQuantity(nutriSum);
						dietNutrientsMap.put(key, rn);
					}
				}
			}
		}
		
	}
	
}
