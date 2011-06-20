import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<iw> ingredients;
    private iw result;

    public SnowBallRecipe(iw result, List<iw> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<iw> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<iw> ingredients) {
        this.ingredients = ingredients;
    }

    public iw getResult() {
        return result;
    }

    public void setResult(iw result) {
        this.result = result;
    }

    public void addIngredient(iw ingredient) {
        if(ingredient!=null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
