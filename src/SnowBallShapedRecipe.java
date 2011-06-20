import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GuntherDW
 */
public class SnowBallShapedRecipe {

    // private List<iw> ingredients;
    private Map<Integer, iw> ingredients;
    private iw result;

    public SnowBallShapedRecipe(iw result) {
        this.result = result;
        this.ingredients = new HashMap<Integer, iw>();
    }

    public SnowBallShapedRecipe(iw result, Map<Integer, iw> ingredients) {
        this.result = result;
        this.ingredients = ingredients;
    }

    public SnowBallShapedRecipe(iw result, List<iw> ing) {
        this.result = result;
        this.ingredients = new HashMap<Integer, iw>();
        for(int x=0; x<9; x++) {
            if(ing.get(x)!=null) {
                ingredients.put(x, ing.get(x));
            }
        }
    }

    public iw getResult() {
        return result;
    }

    public void setResult(iw result) {
        this.result = result;
    }

    public Map<Integer, iw> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<Integer, iw> ingredients) {
        this.ingredients = ingredients;
    }

    public void setIngredientSpot(int spot, iw ingredient) {
        if(spot>=0 && spot < 9) {
            this.ingredients.put(spot, ingredient);
        }
    }

    public iw getIngredientSpot(int spot) {
        if(this.ingredients.containsKey(spot)) {
            return this.ingredients.get(spot);
        } else {
            return null;
        }
    }
}
