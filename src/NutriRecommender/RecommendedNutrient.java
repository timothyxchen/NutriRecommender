//name: Tianxin Chen a-ID:tc2

package hw3;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

//java bean for recommended nutrient
public class RecommendedNutrient {
	private StringProperty nutrientCode = new SimpleStringProperty();
	private FloatProperty nutrientQuantity = new SimpleFloatProperty();

	RecommendedNutrient() {
		this.nutrientCode.set("");
		this.nutrientQuantity.set(0);
	}
	
	RecommendedNutrient(String nutrientCode, Float nutrientQuantity){
		this.nutrientCode.set(nutrientCode);
		this.nutrientQuantity.set(nutrientQuantity);
	}
	
	public final String getNutrientCode() {return this.nutrientCode.get();}
	public final void setNutrientCode(String nutrientCode) {this.nutrientCode.set(nutrientCode);}

	public final Float getNutrientQuantity() {return this.nutrientQuantity.get();}
	public final void setNutrientQuantity(Float nutrientQuantity) {this.nutrientQuantity.set(nutrientQuantity);}
	
	public final StringProperty nutrientCodeProperty() {return nutrientCode;}
	public final FloatProperty nutrientQuantityProperty() {return nutrientQuantity;}
}
