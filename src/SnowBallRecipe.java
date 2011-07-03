import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<iz> ingredients;
    private iz result;

    public SnowBallRecipe(iz result, List<iz> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<iz> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<iz> ingredients) {
        this.ingredients = ingredients;
    }

    public iz getResult() {
        return result;
    }

    public void setResult(iz result) {
        this.result = result;
    }

    public void addIngredient(iz ingredient) {
        if(ingredient!=null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
