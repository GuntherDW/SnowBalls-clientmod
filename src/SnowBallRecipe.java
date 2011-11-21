import java.util.List;

/**
 * @author GuntherDW
 */
public class SnowBallRecipe {

    private List<dk> ingredients;
    private dk result;

    public SnowBallRecipe(dk result, List<dk> ingredients) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<dk> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<dk> ingredients) {
        this.ingredients = ingredients;
    }

    public dk getResult() {
        return result;
    }

    public void setResult(dk result) {
        this.result = result;
    }

    public void addIngredient(dk ingredient) {
        if(ingredient!=null && ingredient.c > 0 && ingredient.c < 1024)
            this.ingredients.add(ingredient);
    }
}
