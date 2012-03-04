import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<aai> ingredients;
    private aai result;

    public SnowBallRecipe(aai result, List<aai> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<aai> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<aai> ingredients) {
        this.ingredients = ingredients;
    }

    public aai getResult() {
        return result;
    }

    public void setResult(aai result) {
        this.result = result;
    }

    public void addIngredient(aai ingredient) {
        if (ingredient != null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
