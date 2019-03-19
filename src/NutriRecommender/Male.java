//name: Tianxin Chen a-ID:tc2
package hw3;

public class Male extends Person{
	float[][] nutriConstantsTableMale = new float[][]{
		//AgeGroups: 3M, 6M, 1Y, 3Y, 8Y, 13Y, 18Y, 30Y, 50Y, ABOVE 
		{1.52f, 1.52f, 1.2f, 1.05f, 0.95f, 0.95f, 0.73f, 0.8f, 0.8f, 0.8f}, //Protein
		{60, 60, 95, 130, 130, 130, 130, 130, 130, 130}, //Carbohydrate
		{19, 19, 19, 19, 25, 31, 38, 38, 38, 30},       //Fiber 
		{36, 36, 32, 21, 16, 17, 15, 14, 14, 14	},  //Histidine
		{88, 88, 43, 28, 22, 22, 21, 19, 19, 19	}, 	//isoleucine
		{156, 156, 93, 63, 49, 49, 47, 42, 42, 42},//leucine
		{107, 107, 89, 58, 46, 46, 43, 38, 38, 38 },//lysine
		{59, 59, 43, 28, 22, 22, 21, 19, 19, 19	}, 	//methionine 
		{59, 59, 43, 28, 22, 22, 21, 19, 19, 19	}, 	//cysteine
		{135, 135, 84, 54, 41, 41, 38, 33, 33, 33 },//phenylalanine 
		{135, 135, 84, 54, 41, 41, 38, 33, 33, 33 },//tyrosine
		{73, 73, 49, 32, 24, 24, 22, 20, 20, 20}, 	//threonine
		{28, 28, 13, 8, 6, 6, 6, 5, 5, 5}, 			//tryptophan
		{87, 87, 58, 37, 28, 28, 27, 24, 24, 24}  	//valine
	};

	Male(float age, float weight, float height, float physicalActivityLevel, String ingredientsToWatch) {
		super(age,weight,height,physicalActivityLevel,ingredientsToWatch);
	}

	@Override
	float calculateEnergyRequirement() {
		if(age<=3) {
			if(age<=0.25) {
				return 89*weight+75;
			}else if(age<=0.5) {
				return 89*weight-44; 
			}else if(age<=1) {
				return 89*weight-78; 
			}else {
				return 89*weight-80; 
			}	
		}else if(age<=18) {
			float A= 88.5f; 
			float B=61.9f; 
			float C=26.7f; 
			int D=903;
			return (A - (B * age) +physicalActivityLevel * (C *weight + D * height / 100) + 20);	
		}else {
			int A= 662; 
			float B=9.53f; 
			float C=15.91f; 
			float D=539.6f;
			return A - (B * age) + physicalActivityLevel * (C * weight + D * height / 100);
		}
	}

	@Override
	void initializeNutriConstantsTable() {
		for(int i =0;i<nutriConstantsTable.length;i++) {
			for(int j =0;j<nutriConstantsTable[i].length;j++) {
				nutriConstantsTable[i][j]= nutriConstantsTableMale[i][j];
			}
		}
	}
}
